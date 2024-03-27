package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;
import lombok.Getter;

import java.util.HashMap;

public class Redirect implements IntoResponse {
    @Getter
    private final String uri;
    private final HashMap<String, String> headers;

    public Redirect(String uri) {
        var headers = new HashMap<String, String>();
        headers.put(ResponseHeaders.LOCATION, uri);

        this.uri = uri;
        this.headers = headers;
    }

    public Redirect(String uri, HashMap<String, String> headers) {
        this(uri);
        this.headers.putAll(headers);
    }

    @Override
    public StatusCode getStatusCode() {
        return StatusCode.SEE_OTHER;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }
}
