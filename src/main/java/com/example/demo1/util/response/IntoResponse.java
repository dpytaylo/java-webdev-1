package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

import java.util.HashMap;
import java.util.Optional;

public interface IntoResponse {
    StatusCode statusCode();
    HashMap<String, String> getHeaders();
    String getContentType();
    byte[] getContent();
    default Optional<String> getTemplate() { return Optional.empty(); }
}
