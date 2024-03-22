package com.example.demo1;

import com.example.demo1.util.Controller;
import com.example.demo1.util.GetMapping;
import com.example.demo1.util.RequestContext;
import com.example.demo1.util.response.DispatcherResponse;
import com.example.demo1.util.response.IntoResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebServlet(name = "multiplicationServlet", value = "/multiplication_servlet")
public class MultiplicationServlet extends Controller {
    @GetMapping("")
    public IntoResponse multiplicate(RequestContext ctx) throws IOException {
        HttpServletRequest request = ctx.getRequest();
        String param = request.getParameter("digit");

        int digit = Integer.parseInt(param);
        int result = digit * 2;

        request.setAttribute("result", result);
        return new DispatcherResponse("pages/result.jsp");
    }
}
