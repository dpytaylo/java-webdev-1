package com.example.demo1.user;

import com.example.demo1.util.*;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.extractor.Json;
import com.example.demo1.util.response.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ControllerMapping("/api/users")
public class JsonUserController extends Controller {
    private static final Logger logger = LogManager.getLogger(JsonUserController.class);
    private final UserService userService;

    public JsonUserController() throws Exception {
        userService = new UserService(new UserRepository());
    }

    @GetMapping("")
    public IntoResponse getAllUsers(RequestContext ctx) throws Exception {
        return new Response<>(new Json<>(new Users(userService.getAll())));
    }
}

