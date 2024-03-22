package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;
import lombok.Data;

import java.util.HashMap;

@Data
public class EmptyResponse implements IntoResponse {
    private final StatusCode statusCode;

    public EmptyResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
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
        return "{}".getBytes();
    }
}
