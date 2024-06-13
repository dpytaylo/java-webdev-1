package com.example.demo1.service;

import com.example.demo1.util.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SessionService {
    public static final String SESSION_USER_ID = "user_id";

    private static final Logger logger = LogManager.getLogger();

    public SessionService() {
    }

    public void createSession(RequestContext ctx, long userId) {
        logger.info("Session was created for the user with " + userId + " id");
        ctx.getRequest().getSession().setAttribute(SESSION_USER_ID, userId);
    }

    public Optional<Long> getSessionUserId(HttpServletRequest request) {
        final var userId = (Long) request.getSession().getAttribute(SESSION_USER_ID);
        return Optional.ofNullable(userId);
    }

    public Optional<Long> getSessionUserId(RequestContext ctx) {
        final var userId = (Long) ctx.getRequest().getSession().getAttribute(SESSION_USER_ID);
        return Optional.ofNullable(userId);
    }
}
