package com.example.demo1.util.response;

import java.util.Optional;

public interface BodyResponse {
    Optional<String> contentType();
    Optional<byte[]> content();
}
