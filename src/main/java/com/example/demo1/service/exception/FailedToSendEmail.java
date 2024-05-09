package com.example.demo1.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FailedToSendEmail extends Exception {
    public FailedToSendEmail(String msg) {
        super(msg);
    }

    public FailedToSendEmail(Exception e) {
        super(e);
    }
}
