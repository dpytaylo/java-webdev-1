package com.example.demo1.util.extractor;

import com.example.demo1.util.response.BodyResponse;

import java.util.Optional;

public record Html(String inner) implements BodyResponse {
    @Override
    public Optional<String> contentType() {
        return Optional.of("text/html; charset=utf-8");
    }

    @Override
    public Optional<byte[]> content() {
        return Optional.of(inner.getBytes());
    }
}
