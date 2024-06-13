package com.example.demo1.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class LocaleService {
    public static final String COOKIE_LOCALE = "locale";

    public static void createLocaleCookie(HttpServletResponse response) {
        setLocaleCookie(response, Locale.of("en"));
    }

    public static void setLocaleCookie(HttpServletResponse response, Locale locale) {
        final var cookie = new Cookie(COOKIE_LOCALE, locale.toString());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(-1);

        response.addCookie(cookie);
    }

    public static Optional<Locale> getLocale(HttpServletRequest request) {
        final var cookies = request.getCookies();

        final var cookie = Arrays
            .stream(cookies)
            .filter(c -> c.getName().equals(COOKIE_LOCALE))
            .findFirst()
            .orElse(null);

        if (cookie == null) {
            return Optional.empty();
        }

        return Optional.of(Locale.of(cookie.getValue()));
    }
}
