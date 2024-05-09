package com.example.demo1.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidConfirmationToken extends Exception {
    public InvalidConfirmationToken(String msg) {
        super(msg);
    }

    public InvalidConfirmationToken(Exception e) {
        super(e);
    }
}
