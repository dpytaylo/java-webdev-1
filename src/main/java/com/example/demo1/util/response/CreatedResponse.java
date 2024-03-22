package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

public class CreatedResponse extends EmptyResponse {
    public CreatedResponse() {
        super(StatusCode.CREATED);
    }
}
