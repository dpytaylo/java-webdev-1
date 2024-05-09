package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;

public record EmptyResponse(StatusCode statusCode) implements IntoResponse {
    public static EmptyResponse OK = new EmptyResponse(StatusCode.OK);

    @Override
    public HashMap<String, String> getHeaders() {
        return ResponseHeaders.EMPTY_HEADERS;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public byte[] getContent() {
        return null;
    }
}
