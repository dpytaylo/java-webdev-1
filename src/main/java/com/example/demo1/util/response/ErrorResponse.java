package com.example.demo1.util.response;

import com.example.demo1.util.extractor.Json;
import com.example.demo1.util.StatusCode;

import java.time.Instant;
import java.util.HashMap;

public class ErrorResponse implements IntoResponse {
    private record ErrorOuter(ErrorInner error) {
        private record ErrorInner(String kind, Instant timestamp) {}
    }

    private final StatusCode statusCode;
    private final ErrorOuter errorData;

    public ErrorResponse(StatusCode statusCode, String kind) {
        this.statusCode = statusCode;
        this.errorData = new ErrorOuter(new ErrorOuter.ErrorInner(kind, Instant.now()));
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return ResponseHeaders.EMPTY_HEADERS;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public byte[] getContent() {
        return new Json<>(errorData).getContent();
    }
}
