package com.example.demo1.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebFilter("/app/*")
public class PerformanceLoggingFilter implements Filter {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        final var startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        final var endTime = System.currentTimeMillis();
        final var url = ((HttpServletRequest) request).getRequestURL().toString();
        logger.info("Request(" + url + ") processed in " + (endTime - startTime) + " ms");
    }
}