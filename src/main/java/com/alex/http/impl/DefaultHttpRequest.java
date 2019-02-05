package com.alex.http.impl;

import com.alex.http.HttpRequest;

import java.util.Collections;
import java.util.Map;

class DefaultHttpRequest implements HttpRequest {
    private final String method;
    private final String uri;
    private final String httpVersion;
    private final String remoteAddress;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

    DefaultHttpRequest(String method, String uri, String httpVersion, String remoteAddress, Map<String, String> headers, Map<String, String> parameters) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.remoteAddress = remoteAddress;
        this.headers = Collections.unmodifiableMap(headers);
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    @Override
    public String getStartingLine() {
        return String.format("%s %s %s", getMethod(), getUri(), getHttpVersion());
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public String getHttpVersion() {
        return this.httpVersion;
    }

    @Override
    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, String> getParameters() {
        return this.parameters;
    }
}
