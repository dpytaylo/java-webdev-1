package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;
import java.util.Optional;

public record EmptyResponse(StatusCode statusCode) implements IntoResponse {
    public static EmptyResponse OK = new EmptyResponse(StatusCode.OK);

    @Override
    public HashMap<String, String> getHeaders() {
        return ResponseHeaders.EMPTY_HEADERS;
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> getContent() {
        return Optional.empty();
    }
}
