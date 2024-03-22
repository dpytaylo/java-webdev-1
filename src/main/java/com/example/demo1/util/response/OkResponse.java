package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

public class OkResponse extends EmptyResponse {
    public OkResponse() {
        super(StatusCode.OK);
    }
}
