package com.example.demo1.util.exception;

public class MainServletException extends Exception {
    public MainServletException(String msg) {
        super(msg);
    }

    public MainServletException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
