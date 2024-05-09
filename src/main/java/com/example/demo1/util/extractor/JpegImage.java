package com.example.demo1.util.extractor;

import com.example.demo1.util.response.BodyResponse;

public record JpegImage(byte[] content) implements BodyResponse {
    @Override
    public String contentType() {
        return "image/jpeg";
    }

    @Override
    public byte[] content() {
        return content;
    }
}
