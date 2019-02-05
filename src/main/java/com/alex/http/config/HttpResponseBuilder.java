package com.alex.http.config;

public interface HttpResponseBuilder {

    ReadableHttpResponse buildNewHttpResponse();

    void prepareHttpResponse(ReadableHttpResponse httpResponse, boolean clearBody);

}
