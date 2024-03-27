package com.example.demo1.util;

import com.example.demo1.exception.UnauthorizedException;
import com.example.demo1.session.SessionRepository;
import com.example.demo1.session.SessionService;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.exception.InvalidRequestUrlException;
import com.example.demo1.util.exception.MainServletException;
import com.example.demo1.util.response.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "mainServlet", value = "/app/*")
public class MainServlet extends HttpServlet {
    private static final String ROOT_PATH = "/app";

    private record ControllerData(String url, Controller controller) {}

    private static final Logger logger = LogManager.getLogger(MainServlet.class);
    private final ArrayList<ControllerData> controllers = new ArrayList<>();
    private final SessionService sessionService;

    private JakartaServletWebApplication application;
    private final TemplateEngine templateEngine = new TemplateEngine();

    public MainServlet() throws MainServletException {
        final var reflections = new Reflections("com.example"); // TODO
        final var allClasses = reflections.getSubTypesOf(Controller.class);

        for (final var cls : allClasses) {
            final var myController = cls.getAnnotation(ControllerMapping.class);

            if (myController == null) {
                logger.fatal("'" + cls.getName() + "' should have the '@MyController' annotation");
                throw new MainServletException("'" + cls.getName() + "' should have the '@MyController' annotation");
            }

            final var url = myController.value();
            final Controller controller = getController(cls);

            controllers.add(new ControllerData(url, controller));
        }

        try {
            new DatabaseConfig();
        } catch (SQLException e) {
            logger.fatal("Failed to initialize database", e);
            throw new RuntimeException(e);
        }

        sessionService = new SessionService(new SessionRepository());
    }

    private static Controller getController(Class<? extends Controller> cls) throws MainServletException {
        final Constructor<?> constructor;
        try {
            constructor = cls.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            logger.fatal("'" + cls.getName() + "' should have an empty constructor\n" + e);
            throw new MainServletException("'" + cls.getName() + "' should have an empty constructor", e);
        }

        final Controller controller;
        try {
            controller = (Controller) constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.fatal("Failed to create an instance of '" + cls.getName() + "'\n" + e);
            throw new MainServletException("Failed to create an instance of '" + cls.getName() + "'", e);
        }
        return controller;
    }

    @Override
    public void init() throws ServletException {
        super.init();

        application = JakartaServletWebApplication.buildApplication(getServletContext());

        final var templateResolver = new WebApplicationTemplateResolver(application);

        // HTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(3600000L);

        // Cache is set to true by default. Set to false if you want templates to be automatically updated when modified.
        templateResolver.setCacheable(true);

        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext ctx;

        try {
            ctx = new RequestContext(application, request, response);
        } catch (InvalidRequestUrlException e) {
            response.setStatus(StatusCode.INTERNAL_SERVER_ERROR.code());
            response.setContentType("text/html; charset=utf-8");
            response.getOutputStream().write("doGet(): Invalid request url".getBytes());
            return;
        }

        processOutput(ctx, doGetInner(ctx));
    }

    private IntoResponse doGetInner(RequestContext ctx) {
        try {
            logger.info("ctx url: " + ctx.getUrl());

            for (final var controllerData : controllers) {
                final var controller = controllerData.controller;

                for (final var method : controller.getGetMethods()) {
                    final var mapping = method.getAnnotation(GetMapping.class);
                    final var output = processMethod(ctx, controller, method, ROOT_PATH + controllerData.url + mapping.value());

                    if (output.isPresent()) {
                        return output.get();
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("doGet() exception", e);
            return TemplateResponse.INTERNAL_SERVER_ERROR;
        }

        return TemplateResponse.NOT_FOUND;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext ctx;

        try {
            ctx = new RequestContext(application, request, response);
        } catch (InvalidRequestUrlException e) {
            response.setStatus(StatusCode.INTERNAL_SERVER_ERROR.code());
            response.setContentType("text/html; charset=utf-8");
            response.getOutputStream().write("doPost(): Invalid request url".getBytes());
            return;
        }

        processOutput(ctx, doPostInner(ctx));
    }

    private IntoResponse doPostInner(RequestContext ctx) {
        try {
            for (final var controllerData : controllers) {
                final var controller = controllerData.controller;

                for (final var method : controller.getPostMethods()) {
                    final var mapping = method.getAnnotation(PostMapping.class);
                    final var output = processMethod(ctx, controller, method, ROOT_PATH + controllerData.url + mapping.value());

                    if (output.isPresent()) {
                        return output.get();
                    }
                }
            }

        } catch (Throwable e) {
            logger.error("doPost() exception", e);
            return TemplateResponse.INTERNAL_SERVER_ERROR;
        }

        return TemplateResponse.NOT_FOUND;
    }

    private Optional<IntoResponse> processMethod(RequestContext ctx, Controller controller, Method method, String url) throws Exception {
        // Ensures that all URLs end with '/' as in RequestContext
        if (!url.endsWith("/")) {
            url += "/";
        }

        var authRequired = method.getAnnotation(AuthRequired.class);

        final var requestUrl = ctx.getUrl();

        if (requestUrl.matches(url) && authRequired != null) {
            long userId;
            try {
                userId = sessionService.getSessionUserId(ctx);
            } catch (UnauthorizedException e) {
                return Optional.of(TemplateResponse.SIGN_IN);
            }

            return Optional.of((IntoResponse) method.invoke(controller, ctx, userId));
        } else if (requestUrl.matches(url)) {
            return Optional.of((IntoResponse) method.invoke(controller, ctx));
        }

        return Optional.empty();
    }

    private void processOutput(RequestContext ctx, IntoResponse output) throws IOException {
        final var response = ctx.getResponse();

        if (output.getTemplate().isPresent()) {
            final var templateName = output.getTemplate().get();
            templateEngine.process(templateName, ctx.getWebContext(), response.getWriter());
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
    }
}
