package com.example.demo1.controller.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadFormInputException extends Exception {
    public BadFormInputException(String cause) {
        super(cause);
    }

    public BadFormInputException(Throwable throwable) {
        super(throwable);
    }
}
