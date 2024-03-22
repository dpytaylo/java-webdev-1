package com.example.demo1.util.extractor;

import com.example.demo1.util.response.BodyResponse;

public record Html(String inner) implements BodyResponse {
    @Override
    public String getContentType() {
        return "text/html; charset=utf-8";
    }

    @Override
    public byte[] getContent() {
        return inner.getBytes();
    }
}
