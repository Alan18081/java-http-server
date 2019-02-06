package com.alex.http.impl;

import com.alex.http.config.HttpResponseBuilder;
import com.alex.http.config.HttpServerConfig;
import com.alex.http.config.ReadableHttpResponse;

import java.util.Date;

public class DefaultHttpResponseBuilder extends AbstractHttpConfigurableComponent implements HttpResponseBuilder {

    DefaultHttpResponseBuilder(DefaultHttpServerConfig serverConfig) {
        super(serverConfig);
    }

    protected ReadableHttpResponse createReadableHttpResponse() {
        return new DefaultReadableHttpResponse();
    }

    @Override
    public ReadableHttpResponse buildNewHttpResponse() {
        ReadableHttpResponse response = createReadableHttpResponse();
        response.setHeader("Date", new Date());
        response.setHeader("Server", httpServerConfig.getServerInfo().getName());
        response.setHeader("Content-Language", "en");
        response.setHeader("Connection", "close");
        response.setHeader("Content-Type", "text/html");
        return response;
    }

    @Override
    public void prepareHttpResponse(ReadableHttpResponse httpResponse, boolean clearBody) {
        if(httpResponse.getStatus() >= 400 && httpResponse.isBodyEmpty()) {

        }
        setContentLength(httpResponse);
        if(clearBody) {
            clearBody(httpResponse);
        }
    }

    private void setContentLength(ReadableHttpResponse response) {
        response.setHeader("Content-Length", response.getBodyLength());
    }

    private void clearBody(ReadableHttpResponse response) {
        response.setBody("");
    }
}
