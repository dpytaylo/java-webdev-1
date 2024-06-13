package com.example.demo1.filter;

import com.example.demo1.service.SessionService;
import com.example.demo1.util.response.Redirect;
import com.example.demo1.util.response.ResponseHeaders;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/app/protected",
    "/app/users/*"
})
public class AuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;

        final var sessionService = new SessionService();
        final var loggedIn = sessionService.getSessionUserId(request).isPresent();

        if (loggedIn) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect(Redirect.SIGN_IN.getHeaders().get(ResponseHeaders.LOCATION));
        }
    }
}
