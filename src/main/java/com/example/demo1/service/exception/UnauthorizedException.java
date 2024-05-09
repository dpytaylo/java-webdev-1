package com.example.demo1.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String msg) {
        super(msg);
    }

    public UnauthorizedException(Exception e) {
        super(e);
    }
}
