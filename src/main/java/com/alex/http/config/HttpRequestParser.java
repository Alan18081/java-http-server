package com.alex.http.config;

import com.alex.http.HttpRequest;
import com.alex.http.exception.HttpServerException;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequestParser {

    HttpRequest parseHttpRequest(InputStream in, String remoteAddress) throws IOException, HttpServerException;

}
