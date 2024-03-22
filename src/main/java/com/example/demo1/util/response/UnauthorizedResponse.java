package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;

public class UnauthorizedResponse implements IntoResponse {
    private final HashMap<String, String> headers;

    public UnauthorizedResponse() {
        var headers = new HashMap<String, String>();
        headers.put(
            ResponseHeaders.WWW_AUTHENTICATE,
            "Bearer realm=\"example\",error=\"invalid_token\",error_description=\"The access token invalid or expired\""
        );

        this.headers = headers;
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.UNAUTHORIZED;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }
}
