package com.example.demo1.controller.exception;

public class BadFormInputException extends Exception {
    public BadFormInputException(String msg) {
        super(msg);
    }
}
