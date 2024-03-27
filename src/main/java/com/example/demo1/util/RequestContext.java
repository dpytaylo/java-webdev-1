package com.example.demo1.util;

import com.example.demo1.util.exception.InvalidRequestUrlException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.regex.Pattern;

@Data
public class RequestContext {
    private static final String URL_REGEX = "http://[^/]+(:\\d+)?/demo1_war_exploded(/[^?#]*)";
    private static final Pattern PATTERN = Pattern.compile(URL_REGEX);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String url;

    private WebContext webContext;

    public RequestContext(JakartaServletWebApplication application, HttpServletRequest request, HttpServletResponse response) throws InvalidRequestUrlException {
        this.request = request;
        this.response = response;

        final var matcher = PATTERN.matcher(request.getRequestURL().toString());

        if (matcher.find()) {
            this.url = matcher.group(2);

            // Ensures that all URLs end with '/'
            if (!this.url.endsWith("/")) {
                this.url += "/";
            }
        } else {
            throw new InvalidRequestUrlException();
        }

        final var exchange = application.buildExchange(request, response);
        this.webContext = new WebContext(exchange, exchange.getLocale());
    }
}
