package com.alex.http.config;

import java.util.Map;
import com.alex.http.HttpResponse;

public interface ReadableHttpResponse extends HttpResponse {

    int getStatus();

    Map<String, String> getHeaders();

    byte[] getBody();

    boolean isBodyEmpty();

    int getBodyLength();
}
