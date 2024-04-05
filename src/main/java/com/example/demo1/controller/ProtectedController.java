package com.example.demo1.controller;

import com.example.demo1.util.*;
import com.example.demo1.util.annotation.AuthRequired;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.ControllerMapping;
import com.example.demo1.util.extractor.Html;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Response;

@ControllerMapping("/protected")
public class ProtectedController extends Controller {
    @GetMapping("/")
    @AuthRequired
    public IntoResponse get(RequestContext ctx, long userId) {
        return new Response<>(StatusCode.OK, new Html("user id = " + userId));
    }
}
