package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

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
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return ResponseHeaders.EMPTY_HEADERS;
    }

    @Override
    public String getContentType() {
        return body.getContentType();
    }

    @Override
    public byte[] getContent() {
        return body.getContent();
    }
}
