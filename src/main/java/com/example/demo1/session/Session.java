package com.example.demo1.session;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Session {
    private String sessionId;
    private String userId;
}
