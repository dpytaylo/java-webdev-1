package com.example.demo1.filter;

import com.example.demo1.util.response.Redirect;
import com.example.demo1.util.response.ResponseHeaders;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/")
public class RootFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        final var response = (HttpServletResponse) servletResponse;

        response.sendRedirect(Redirect.ROOT.getHeaders().get(ResponseHeaders.LOCATION));
    }
}
