package com.example.demo1;

import com.example.demo1.session.SessionRepository;
import com.example.demo1.session.SessionService;
import com.example.demo1.user.User;
import com.example.demo1.user.UserRepository;
import com.example.demo1.user.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.MyController;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.*;
import jakarta.servlet.http.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

@MyController("/sign_in")
public class SignInController extends Controller {
    private static final String VAR_ERROR = "error";
    private static final Logger logger = LogManager.getLogger(SignInController.class);
    private final UserService userService;
    private final SessionService sessionService;

    public SignInController() throws Exception {
        userService = new UserService(new UserRepository());
        sessionService = new SessionService(new SessionRepository());
    }

    @GetMapping("/")
    public IntoResponse get(RequestContext ctx) {
        final var errorParam = ctx.getRequest().getParameter(VAR_ERROR);
        ctx.getWebContext().setVariable(VAR_ERROR, errorParam);

        return TemplateResponse.SIGN_IN;
    }

    @PostMapping("/")
    public IntoResponse signIn(RequestContext ctx) throws SQLException {
        final var request = ctx.getRequest();

        final var email = request.getParameter("email");
        final var password = request.getParameter("password");

        User user;
        try {
            final var userOptional = userService.signIn(email, password);
            if (userOptional.isEmpty()) {
                ctx.getWebContext().setVariable(VAR_ERROR, "Invalid email or password");
                return TemplateResponse.SIGN_IN;
            }

            user = userOptional.get();
        } catch (SQLException e) {
            logger.error("Failed to sign in", e);
            return TemplateResponse.INTERNAL_SERVER_ERROR;
        }

        sessionService.createSessionCookie(ctx, user.getId());
        return TemplateResponse.ROOT;
    }
}
