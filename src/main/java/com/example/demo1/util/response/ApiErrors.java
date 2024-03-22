package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;

public class ApiErrors {
    public static final ErrorResponse INVALID_JSON = new ErrorResponse(StatusCode.BAD_REQUEST, "InvalidJson");
    public static final ErrorResponse NOT_FOUND = new ErrorResponse(StatusCode.NOT_FOUND, "NotFoundError");
    public static final ErrorResponse INTERNAL_SERVER_ERROR = new ErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "InternalServerError");
}
