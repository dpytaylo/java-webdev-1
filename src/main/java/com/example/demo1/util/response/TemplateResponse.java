package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;
import java.util.Optional;

public record TemplateResponse(String name) implements IntoResponse {
    public static final TemplateResponse NOT_FOUND = new TemplateResponse("errors/not_found");
    public static final TemplateResponse INTERNAL_SERVER_ERROR = new TemplateResponse("errors/internal_server_error");

    public static final TemplateResponse ROOT = new TemplateResponse("index");
    public static final TemplateResponse USERS = new TemplateResponse("users");
    public static final TemplateResponse SIGN_UP = new TemplateResponse("sign_up");
    public static final TemplateResponse SIGN_IN = new TemplateResponse("sign_in");
    public static final TemplateResponse MODIFY = new TemplateResponse("users_modify");
    public static final TemplateResponse CONFIRM = new TemplateResponse("confirm");

    @Override
    public StatusCode statusCode() {
        return null;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }

    @Override
    public Optional<String> getTemplate() {
        return Optional.of(name);
    }
}
