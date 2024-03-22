package com.example.demo1.user;

import com.example.demo1.util.*;
import com.example.demo1.util.extractor.Json;
import com.example.demo1.util.response.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(name = "userController", value = "/api/users/*")
public class UserController extends Controller {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private UserService userService;

    @Override
    public void initController() throws Exception {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    public IntoResponse getAllUsers(RequestContext ctx) throws Exception {
        return new Response<>(new Json<>(new Users(userService.getAll())));
    }

    @PostMapping("")
    public IntoResponse createUser(RequestContext ctx) throws Exception {
        Json<UserDto> json;
        try {
            json = Json.fromReader(ctx.getRequest().getReader(), UserDto.class);
        } catch (IOException e) {
            return ApiErrors.INVALID_JSON;
        }

        userService.create(json.getInner());
        return new CreatedResponse();
    }
}

