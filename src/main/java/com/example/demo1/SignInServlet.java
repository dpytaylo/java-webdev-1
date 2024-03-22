package com.example.demo1;

import com.example.demo1.user.UserRepository;
import com.example.demo1.user.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.extractor.Html;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Redirect;
import com.example.demo1.util.response.Response;
import com.example.demo1.util.response.ResponseHeaders;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@WebServlet(name = "signInServlet", value = "/sign_in/*")
public class SignInServlet extends Controller {
    private static final Logger logger = LogManager.getLogger(CreateUserServlet.class);

    private UserService userService;

    @Override
    protected void initController() throws Exception {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    public IntoResponse get(RequestContext ctx) {
        String error = "";
        Object errorParam = ctx.getRequest().getParameter("error");
        if (errorParam != null) {
            error = "<div class=\"error-box\">" + errorParam + "</div>";
        }

        String html = "<!DOCTYPE html>" +
            "<html><head><title>My App</title><link rel=\"stylesheet\" href=\"index.css\"></head><body>" +
            "<div class=\"middle-box\">" +
            "<p class=\"text-xl\">Sign In</p>" +
            error +
            "<form action=\"sign_in\" method=\"post\">" +
            "   <label for=\"email\">Email</label><br>" +
            "   <input type=\"email\" id=\"email\" name=\"email\" autocomplete=\"email\" required><br>" +
            "   <label for=\"password\">Password</label><br>" +
            "   <input type=\"password\" id=\"password\" name=\"password\" autocomplete=\"current-password\" required><br>" +
            "   <input type=\"submit\" value=\"Submit\">" +
            "</form>" +
            "</div>" +
            "</body></html>";

        return new Response<>(StatusCode.OK, new Html(html));
    }

    @PostMapping("")
    public IntoResponse signIn(RequestContext ctx) throws Exception {
        HttpServletRequest request = ctx.getRequest();

        logger.info("signIn()");
        logger.info(request);
        logger.info("sign_in: email=" + request.getParameter("email") + ", password=" + request.getParameter("password"));
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // TODO
        if (email.equals("a@gmail.com") && password.equals("password")) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ResponseHeaders.AUTHORIZATION, JwtTokens.generateBearerToken(1));

            return new Redirect(request.getContextPath(), headers);
        } else {
            String error = "Invalid email or password";
            return new Redirect("sign_in?error=" + URLEncoder.encode(error, StandardCharsets.UTF_8));
        }
    }
}
