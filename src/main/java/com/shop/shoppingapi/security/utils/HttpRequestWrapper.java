package com.shop.shoppingapi.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> customParams = new HashMap<>();

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addParameter(String name, String value) {
        customParams.put(name, new String[]{value});
    }

    @Override
    public String getParameter(String name) {
        if (customParams.containsKey(name)) {
            return customParams.get(name)[0];
        }
        return super.getParameter(name);
    }
}
