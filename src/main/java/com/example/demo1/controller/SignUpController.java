package com.example.demo1.controller;

import com.example.demo1.controller.exception.BadFormInputException;
import com.example.demo1.repository.SessionRepository;
import com.example.demo1.service.SessionService;
import com.example.demo1.entity.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.service.UserService;
import com.example.demo1.util.Controller;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.TemplateResponse;

import java.sql.SQLException;

@ControllerMapping("/sign_up")
public class SignUpController extends Controller {
    private static final String VAR_ERROR = "error";
    private final UserService userService;
    private final SessionService sessionService;

    public SignUpController() {
        this.userService = new UserService(new UserRepository());
        this.sessionService = new SessionService(new SessionRepository());
    }

    @GetMapping("/")
    public IntoResponse getRoot(RequestContext ctx) {
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
        } catch (BadFormInputException e) {
            ctx.getWebContext().setVariable(VAR_ERROR, e.getMessage());
            return TemplateResponse.SIGN_UP;
        }

        sessionService.createSessionCookie(ctx, user.getId());
        return TemplateResponse.ROOT;
    }

    @GetMapping("/confirmation")
    public IntoResponse getConfirmation(RequestContext ctx) {
        final var errorParam = ctx.getRequest().getParameter("error");
        ctx.getWebContext().setVariable(VAR_ERROR, errorParam);

        return TemplateResponse.SIGN_UP;
    }
}
