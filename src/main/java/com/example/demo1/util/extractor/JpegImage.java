package com.example.demo1.util.extractor;

import com.example.demo1.util.response.BodyResponse;

import java.util.Optional;

public record JpegImage(byte[] inner) implements BodyResponse {
    @Override
    public Optional<String> contentType() {
        return Optional.of("image/jpeg");
    }

    @Override
    public Optional<byte[]> content() {
        return Optional.of(inner);
    }
}
