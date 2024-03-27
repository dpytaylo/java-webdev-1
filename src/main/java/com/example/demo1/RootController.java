package com.example.demo1;

import com.example.demo1.util.Controller;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.MyController;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.TemplateResponse;

@MyController("/")
public class RootController extends Controller {
    @GetMapping("")
    public IntoResponse getHome(RequestContext ctx) {
        return TemplateResponse.ROOT;
    }
}
