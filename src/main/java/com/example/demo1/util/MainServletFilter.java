package com.example.demo1.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class MainServletFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final var req = (HttpServletRequest) request;
        final var path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.startsWith("/assets/") || path.startsWith("/WEB-INF/")) {
            chain.doFilter(req, response); // Goes to the default servlet
        } else {
            req.getRequestDispatcher("/app" + path).forward(req, response);
        }
    }
}
