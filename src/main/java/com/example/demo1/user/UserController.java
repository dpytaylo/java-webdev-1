package com.example.demo1.user;

import com.example.demo1.util.*;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.response.*;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@ControllerMapping("/users")
public class UserController extends Controller {
    private final UserService userService;

    private static final Pattern PATTERN = Pattern.compile("/app/users/modify/(\\d+)/");

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
    public IntoResponse modify(RequestContext ctx, long currentUserId) throws SQLException {
        final var matcher = PATTERN.matcher(ctx.getUrl());
        final var userId = Integer.parseInt(matcher.group(1));

        final var user = userService.findById(userId);
        ctx.getWebContext().setVariable("email", user.getEmail());
        ctx.getWebContext().setVariable("password", user.getEmail());
        ctx.getWebContext().setVariable("name", user.getName());
        ctx.getWebContext().setVariable("age", user.getAge());
        return TemplateResponse.MODIFY;
    }
}
