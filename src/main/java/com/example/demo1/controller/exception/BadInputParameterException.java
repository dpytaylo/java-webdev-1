package com.example.demo1.controller.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadInputParameterException extends Exception {
    public BadInputParameterException(String cause) {
        super(cause);
    }

    public BadInputParameterException(Throwable throwable) {
        super(throwable);
    }
}
