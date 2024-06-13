package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Optional;

@Data
@AllArgsConstructor
public class Response<T extends BodyResponse> implements IntoResponse {
    private final StatusCode statusCode;
    private final T body;

    public Response(T body) {
        this.statusCode = StatusCode.OK;
        this.body = body;
    }

    @Override
    public StatusCode statusCode() {
        return statusCode;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return ResponseHeaders.EMPTY_HEADERS;
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.of(body.contentType());
    }

    @Override
    public Optional<byte[]> getContent() {
        return Optional.of(body.content());
    }
}
