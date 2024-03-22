package com.example.demo1;

import java.io.*;

import com.example.demo1.util.Controller;
import com.example.demo1.util.GetMapping;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.StatusCode;
import com.example.demo1.util.extractor.Html;
import com.example.demo1.util.response.IntoResponse;
import com.example.demo1.util.response.Response;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello_servlet")
public class HelloServlet extends Controller {
    private String message;

    @Override
    public void initController() {
        message = "Hello World!";
    }

    @GetMapping("")
    public IntoResponse hello(RequestContext ctx) throws IOException {
        String out = "<html><body>" +
            "<h1>" + message + "</h1>" +
            "</body></html>";

        return new Response<>(StatusCode.OK, new Html(out));
    }
}