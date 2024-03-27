package com.example.demo1.user;

import com.example.demo1.util.*;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.extractor.Json;
import com.example.demo1.util.response.*;

@ControllerMapping("/api/users")
public class JsonUserController extends Controller {
    private final UserService userService;

    public JsonUserController() {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    public IntoResponse getAllUsers(RequestContext ctx) throws Exception {
        return new Response<>(new Json<>(new Users(userService.getAll())));
    }
}

