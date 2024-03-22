package com.example.demo1.util.response;

import com.example.demo1.util.extractor.Json;
import com.example.demo1.util.StatusCode;

import java.time.Instant;
import java.util.HashMap;

public class ErrorResponseWithDetails<T> implements IntoResponse {
    private record ErrorOuter<T>(ErrorInner<T> error) {
        private record ErrorInner<T>(String kind, Instant timestamp, T details) {}
    }

    private final StatusCode statusCode;
    private final ErrorOuter<T> errorData;

    public ErrorResponseWithDetails(StatusCode statusCode, String kind, T details) {
        this.statusCode = statusCode;
        this.errorData = new ErrorOuter<>(new ErrorOuter.ErrorInner<>(kind, Instant.now(), details));
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