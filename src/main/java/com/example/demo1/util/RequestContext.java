package com.example.demo1.util;

import com.example.demo1.service.LocaleService;
import com.example.demo1.util.exception.InvalidRequestUrlException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Data
public class RequestContext {
    private static final Logger logger = LogManager.getLogger();

    private static final String URL_REGEX = "http://[^/]+(:\\d+)?/demo1_war_exploded(/[^?#]*)";
    private static final Pattern PATTERN = Pattern.compile(URL_REGEX);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String url;
    private HttpSession session;
    private Collection<Part> parts;

    private WebContext webContext;

    public RequestContext(JakartaServletWebApplication application, HttpServletRequest request, HttpServletResponse response)
        throws InvalidRequestUrlException, IOException, ServletException
    {
        this.request = request;
        this.response = response;

        final var matcher = PATTERN.matcher(request.getRequestURL().toString());

        final var contentType = request.getContentType();
        if (contentType != null && contentType.equals("multipart/form-data")) {
            this.parts = request.getParts();
        } else {
            this.parts = List.of();
        }

        if (matcher.find()) {
            this.url = matcher.group(2);

            // Ensures that all URLs end with '/'
            if (!this.url.endsWith("/")) {
                this.url += "/";
            }
        } else {
            throw new InvalidRequestUrlException();
        }

        this.session = request.getSession();

        final var localeOptional = LocaleService.getLocale(request);
        if (localeOptional.isEmpty()) {
            LocaleService.createLocaleCookie(response);
        }

        final var locale = localeOptional.orElse(Locale.of("en"));
        final var exchange = application.buildExchange(request, response);
        this.webContext = new WebContext(exchange, locale);
    }
}
