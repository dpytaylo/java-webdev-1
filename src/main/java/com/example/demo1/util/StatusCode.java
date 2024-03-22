package com.example.demo1.util;

public record StatusCode(int code) {
    public static final StatusCode OK = new StatusCode(200);
    public static final StatusCode CREATED = new StatusCode(201);
    public static final StatusCode SEE_OTHER = new StatusCode(303);
    public static final StatusCode BAD_REQUEST = new StatusCode(400);
    public static final StatusCode UNAUTHORIZED = new StatusCode(401);
    public static final StatusCode NOT_FOUND = new StatusCode(404);
    public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(500);
}
