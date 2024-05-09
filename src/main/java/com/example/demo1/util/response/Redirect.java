package com.example.demo1.util.response;

import com.example.demo1.util.StatusCode;
import lombok.Getter;

import java.util.HashMap;

public class Redirect implements IntoResponse {
    private static final String PREFIX = "/demo1_war_exploded";
    public static final Redirect ROOT = new Redirect(PREFIX + "/");
    public static final Redirect USERS = new Redirect(PREFIX + "/users");
    public static final Redirect CONFIRM = new Redirect(PREFIX + "/sign_up/confirm");
    public static final Redirect DEFAULT_AVATAR = new Redirect(PREFIX + "/assets/default_avatar.jpg");

    @Getter
    private final String uri;
    private final HashMap<String, String> headers;

    public Redirect(String uri) {
        var headers = new HashMap<String, String>();
        headers.put(ResponseHeaders.LOCATION, uri);

        this.uri = uri;
        this.headers = headers;
    }

    @Override
    public StatusCode statusCode() {
        return StatusCode.SEE_OTHER;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }
}
