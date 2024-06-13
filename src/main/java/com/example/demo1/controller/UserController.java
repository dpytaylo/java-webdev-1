package com.example.demo1.controller;

import com.example.demo1.controller.exception.BadFormInputException;
import com.example.demo1.controller.exception.BadInputParameterException;
import com.example.demo1.entity.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.service.UserService;
import com.example.demo1.util.*;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.annotation.PostMapping;
import com.example.demo1.util.extractor.JpegImage;
import com.example.demo1.util.response.*;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@ControllerMapping("/users")
public class UserController extends Controller {
    private final UserService userService;

    private static final Pattern MODIFY_PATTERN = Pattern.compile("/app/users/(\\d+)/modify/?");
    private static final Pattern AVATAR_PATTERN = Pattern.compile("/app/users/(\\d+)/avatar/?");

    public UserController() {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("/")
    public IntoResponse pageRoot(RequestContext ctx) {
        List<User> users;

        try {
            users = userService.getAll();
        } catch (SQLException e) {
            return TemplateResponse.INTERNAL_SERVER_ERROR;
        }

        ctx.getWebContext().setVariable("users", users);
        return TemplateResponse.USERS;
    }

    @GetMapping("/\\d+/modify")
    @AuthRequired
    public IntoResponse pageModify(RequestContext ctx, long currentUserId) throws SQLException {
        final var matcher = MODIFY_PATTERN.matcher(ctx.getUrl());

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

    @PostMapping("/\\d+/modify")
    @AuthRequired
    public IntoResponse modify(RequestContext ctx, long currentUserId) throws BadInputParameterException, SQLException {
        final var matcher = MODIFY_PATTERN.matcher(ctx.getUrl());

        long userId;
        if (matcher.find()) {
            userId = Integer.parseInt(matcher.group(1));
        } else {
            throw new BadInputParameterException("expected valid user id");
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

    @GetMapping("/\\d+/avatar")
    public IntoResponse imageAvatar(RequestContext ctx) throws BadInputParameterException, SQLException {
        final var matcher = AVATAR_PATTERN.matcher(ctx.getUrl());

        long userId;
        if (matcher.find()) {
            userId = Integer.parseInt(matcher.group(1));
        } else {
            throw new BadInputParameterException("expected valid user id");
        }

        final var avatar = userService.getAvatarByUserId(userId);
        if (avatar.isEmpty()) {
            return Redirect.DEFAULT_AVATAR;
        }

        return new Response<>(new JpegImage(avatar.get()));
    }

    @PostMapping("/\\d+/avatar")
    public IntoResponse modifyAvatar(RequestContext ctx) throws Exception {
        final var matcher = AVATAR_PATTERN.matcher(ctx.getUrl());

        long userId;
        if (matcher.find()) {
            userId = Integer.parseInt(matcher.group(1));
        } else {
            throw new BadInputParameterException("expected valid user id");
        }

        final var file = ctx.getRequest().getPart("file");
        userService.modifyAvatar(userId, file.getInputStream().readAllBytes());
        return Redirect.USERS;
    }
}
