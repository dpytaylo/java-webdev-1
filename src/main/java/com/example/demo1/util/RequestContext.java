package com.example.demo1.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class RequestContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String url;

    public RequestContext(String endpointUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
        URL requestFullUrl = new URL(request.getRequestURL().toString());
        String requestUrl = requestFullUrl.getPath();

        ArrayList<String> requestUrlArray = new ArrayList<>(Arrays.asList(requestUrl.split("/")));
        requestUrlArray.removeIf(String::isEmpty);
        requestUrlArray.remove(0); // Remove the first element with the name our application ('demo1_war_exploded/')

        ArrayList<String> endpointUrlArray = new ArrayList<>(Arrays.asList(endpointUrl.split("/")));
        endpointUrlArray.removeIf(String::isEmpty);

        // Compares url heads
        if (!requestUrlArray.subList(0, endpointUrlArray.size()).equals(endpointUrlArray)) {
            throw new Exception("Invalid endpointUrl");
        }

        this.request = request;
        this.response = response;
        this.url = String.join("/", requestUrlArray.subList(endpointUrlArray.size(), requestUrlArray.size()));
    }

    public boolean matchesApiUrl(String url) {
        return this.url.matches(url);
    }
}
