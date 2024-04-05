package com.example.demo1.controller;

import com.example.demo1.controller.exception.BadFormInputException;
import com.example.demo1.entity.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.service.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.response.*;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@ControllerMapping("/users")
public class UserController extends Controller {
    private final UserService userService;

    private static final Pattern PATTERN = Pattern.compile("/app/users/modify/(\\d+)/?");

    public UserController() {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("/")
    public IntoResponse get(RequestContext ctx) {
        List<User> users;

        try {
            users = userService.getAll();
        } catch (SQLException e) {
            return TemplateResponse.INTERNAL_SERVER_ERROR;
        }

        ctx.getWebContext().setVariable("users", users);
        return TemplateResponse.USERS;
    }

    @GetMapping("/modify/\\d+")
    @AuthRequired
    public IntoResponse getModify(RequestContext ctx, long currentUserId) throws SQLException {
        final var matcher = PATTERN.matcher(ctx.getUrl());

        long userId;
        if (matcher.find()) {
            userId = Integer.parseInt(matcher.group(1));
        } else {
            throw new RuntimeException("Unreachable");
        }

        final var webContext = ctx.getWebContext();
        final var user = userService.findById(userId);

        webContext.setVariable("id", user.getId());
        webContext.setVariable("email", user.getEmail());
        webContext.setVariable("password", user.getEmail());
        webContext.setVariable("name", user.getName());
        webContext.setVariable("age", user.getAge());

        return TemplateResponse.MODIFY;
    }

    @PostMapping("/modify/\\d+")
    @AuthRequired
    public IntoResponse modify(RequestContext ctx, long currentUserId) throws SQLException {
        final var matcher = PATTERN.matcher(ctx.getUrl());

        long userId;
        if (matcher.find()) {
            userId = Integer.parseInt(matcher.group(1));
        } else {
            throw new RuntimeException("Unreachable");
        }

        final var request = ctx.getRequest();

        try {
            userService.modify(
                userId,
                request.getParameter("name"),
                Integer.parseInt(request.getParameter("age"))
            );
        } catch (BadFormInputException e) {
            ctx.getWebContext().setVariable("error", e.getMessage());
            return TemplateResponse.MODIFY;
        }

        return Redirect.USERS;
    }
}
