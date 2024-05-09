package com.example.demo1.controller;

import com.example.demo1.controller.exception.BadFormInputException;
import com.example.demo1.repository.ConfirmationTokenRepository;
import com.example.demo1.repository.SessionRepository;
import com.example.demo1.service.ConfirmationTokenService;
import com.example.demo1.service.SessionService;
import com.example.demo1.entity.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.service.UserService;
import com.example.demo1.service.exception.FailedToSendEmail;
import com.example.demo1.util.Controller;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Redirect;
import com.example.demo1.util.response.TemplateResponse;

import java.sql.SQLException;

@ControllerMapping("/sign_up")
public class SignUpController extends Controller {
    private static final String VAR_ERROR = "error";
    private final UserService userService;
    private final SessionService sessionService;
    private final ConfirmationTokenService confirmationTokenService;

    public SignUpController() {
        this.userService = new UserService(new UserRepository());
        this.sessionService = new SessionService(new SessionRepository());
        this.confirmationTokenService = new ConfirmationTokenService(
            new ConfirmationTokenRepository(),
            new UserRepository()
        );
    }

    @GetMapping("/")
    public IntoResponse pageRoot(RequestContext ctx) {
        final var error = ctx.getWebContext().getVariable(VAR_ERROR);
        ctx.getWebContext().setVariable(VAR_ERROR, error);

        return TemplateResponse.SIGN_UP;
    }

    @PostMapping("/")
    public IntoResponse createUser(RequestContext ctx) throws SQLException {
        final var request = ctx.getRequest();
        final var email = request.getParameter("email");
        final var password = request.getParameter("password");
        final var name = request.getParameter("name");
        final var age = Integer.parseInt(request.getParameter("age"));

        User user;
        try {
            user = userService.signUp(email, password, name, age);
        } catch (FailedToSendEmail | BadFormInputException e) {
            ctx.getWebContext().setVariable(VAR_ERROR, e.getMessage());
            return TemplateResponse.SIGN_UP;
        }

        sessionService.createSessionCookie(ctx, user.getId());
        return Redirect.CONFIRM;
    }

    @GetMapping("/confirm")
    public IntoResponse pageConfirm(RequestContext ctx) {
        return TemplateResponse.CONFIRM;
    }

    @PostMapping("/confirm")
    @AuthRequired
    public IntoResponse confirmEmail(RequestContext ctx, long userId) throws SQLException {
        final var request = ctx.getRequest();
        final var token = request.getParameter("token");

        if (!confirmationTokenService.checkConfirmationToken(userId, token)) {
            ctx.getWebContext().setVariable(VAR_ERROR, "Invalid token");
            return TemplateResponse.CONFIRM;
        }

        return Redirect.ROOT;
    }
}
