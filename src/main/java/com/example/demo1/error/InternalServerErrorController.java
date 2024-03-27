package com.example.demo1.error;

import com.example.demo1.util.Controller;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.annotation.GetMapping;
import com.example.demo1.util.annotation.MyController;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.TemplateResponse;

@MyController("/errors/internal_server_error")
public class InternalServerErrorController extends Controller {
    @GetMapping("/")
    public IntoResponse getRoot(RequestContext ctx) {
        return TemplateResponse.INTERNAL_SERVER_ERROR;
    }
}
