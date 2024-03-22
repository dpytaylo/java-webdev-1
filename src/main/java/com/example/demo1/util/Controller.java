package com.example.demo1.util;

import com.example.demo1.util.response.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(Controller.class);

    protected String webServletUrl;
    private List<Method> getMethods;
    private List<Method> postMethods;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            var webServletClass = this.getClass().getAnnotation(WebServlet.class);
            if (webServletClass.value().length != 1) {
                throw new Exception("Excepted only one URL");
            }

            // '/api/users/*' to '/api/users/'
            webServletUrl = webServletClass.value()[0].replaceAll("\\*$", "");

            var getMethods = Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .toList();

            var postMethods = Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(PostMapping.class))
                .toList();

            this.getMethods = getMethods;
            this.postMethods = postMethods;

            logger.info("All methods: " + Arrays.toString(this.getClass().getMethods()));
            logger.info("Get methods: " + getMethods);
            logger.info("Post methods: " + postMethods);

            initController();
        } catch (Exception e) {
            logger.error("Failed to initialize the controller", e);
            throw new RuntimeException("InternalServerError");
        }
    }

    protected void initController() throws Exception {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IntoResponse output = null;

        try {
            var ctx = new RequestContext(webServletUrl, request, response);

            for (var method : getMethods) {
                var mapping = method.getAnnotation(GetMapping.class);
                output = processMethod(ctx, method, mapping.value());

                if (output != null) {
                    break;
                }
            }

            if (output == null) {
                throw new Exception("Failed to find the method");
            }
        } catch (Throwable e) {
            logger.error("doGet() exception", e);
            output = ApiErrors.INTERNAL_SERVER_ERROR;
        }

        processOutput(request, response, output);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IntoResponse output = null;

        try {
            var ctx = new RequestContext(webServletUrl, request, response);

            for (var method : postMethods) {
                var mapping = method.getAnnotation(PostMapping.class);
                output = processMethod(ctx, method, mapping.value());

                if (output != null) {
                    break;
                }
            }

            if (output == null) {
                throw new Exception("Failed to find the method");
            }
        } catch (Throwable e) {
            logger.error("doPost() exception", e);
            output = ApiErrors.INTERNAL_SERVER_ERROR;
        }

        processOutput(request, response, output);
    }

    private IntoResponse processMethod(RequestContext ctx, Method method, String mappingValue) throws Exception {
        var authRequired = method.getAnnotation(AuthRequired.class);

        if (ctx.matchesApiUrl(mappingValue) && authRequired != null) {
            var authorization = ctx.getRequest().getHeader(RequestHeader.AUTHORIZATION);

            logger.info("token = " + authorization);
            long userId;
            try {
                userId = JwtTokens.parseBearerToken(authorization);
            } catch (Exception e) {
                logger.error("Invalid or expired token: " + authorization);
                return new UnauthorizedResponse();
            }

            return (IntoResponse) method.invoke(this, ctx, userId);
        } else if (ctx.matchesApiUrl(mappingValue)) {
            return (IntoResponse) method.invoke(this, ctx);
        }

        return null;
    }

    private void processOutput(HttpServletRequest request, HttpServletResponse response, IntoResponse output) throws IOException {
        if (output.requestDispatcherResponse().isPresent()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher(output.requestDispatcherResponse().get());

            try {
                dispatcher.forward(request, response);
            } catch (ServletException e2) {
                logger.error("Failed to forward request", e2);
                response.setStatus(StatusCode.INTERNAL_SERVER_ERROR.code());
            }

            return;
        }

        response.setStatus(output.getStatusCode().code());

        for (Map.Entry<String, String> entry : output.getHeaders().entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }

        response.setContentType(output.getContentType());
        response.getOutputStream().write(output.getContent());
    }

    @Override
    public void destroy() {
        super.destroy();

        postMethods = null;
        getMethods = null;
        webServletUrl = null;
    }
}