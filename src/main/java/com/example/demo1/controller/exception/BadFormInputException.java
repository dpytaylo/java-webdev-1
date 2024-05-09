package com.example.demo1.controller.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadFormInputException extends Exception {
    public BadFormInputException(String msg) {
        super(msg);
    }

    public BadFormInputException(Exception e) {
        super(e);
    }
}
