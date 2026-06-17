package com.hermens.backend.model;

import java.util.Map;

public class HermensProxyRequest {
    private String path;
    private String method;
    private Map<String, Object> payload;

    public HermensProxyRequest() {
    }

    public HermensProxyRequest(String path, String method, Map<String, Object> payload) {
        this.path = path;
        this.method = method;
        this.payload = payload;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method == null ? "GET" : method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
