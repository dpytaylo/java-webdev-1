package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;
import java.util.Optional;

public record DispatcherResponse(String path) implements IntoResponse {
    @Override
    public StatusCode getStatusCode() {
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
    public Optional<String> requestDispatcherResponse() {
        return Optional.of(path);
    }
}
