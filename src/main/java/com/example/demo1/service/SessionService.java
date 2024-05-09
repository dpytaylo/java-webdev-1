package com.example.demo1.service;

import com.example.demo1.service.exception.UnauthorizedException;
import com.example.demo1.repository.SessionRepository;
import com.example.demo1.util.RequestContext;
import jakarta.servlet.http.Cookie;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class SessionService {
    private static final String SESSION_COOKIE_NAME = "session";
    private static final String HOSTNAME = "localhost";
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void createSessionCookie(RequestContext ctx, long userId) throws SQLException {
        String sessionId;
        do {
            sessionId = UUID.randomUUID().toString();
        } while (sessionRepository.findBySessionId(sessionId).isPresent());

        sessionRepository.insert(sessionId, userId);

        cleanCookies(ctx);

        final var cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain(HOSTNAME);
        cookie.setMaxAge(-1);

        ctx.getResponse().addCookie(cookie);
    }

    public long getSessionUserId(RequestContext ctx) throws UnauthorizedException, SQLException {
        final var cookies = ctx.getRequest().getCookies();

        final var cookie = Arrays
            .stream(cookies)
            .filter(c -> c.getName().equals(SESSION_COOKIE_NAME))
            .findFirst()
            .orElseThrow(UnauthorizedException::new);

        final var userId = sessionRepository.findBySessionId(cookie.getValue());
        if (userId.isEmpty()) {
            throw new UnauthorizedException();
        }

        return userId.get();
    }

    private void cleanCookies(RequestContext ctx) {
        final var cookies = ctx.getRequest().getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getDomain() != null) {
                if (!cookie.getDomain().equals(HOSTNAME)) {
                    continue;
                }
            }

            if (cookie.getName().equals(SessionService.SESSION_COOKIE_NAME)) {
                cookie.setMaxAge(0);
                ctx.getResponse().addCookie(cookie);
            }
        }
    }
}
