package com.alex.http.impl;

import com.alex.http.HttpRequest;
import com.alex.http.HttpResponse;
import com.alex.http.HttpServerContext;
import com.alex.http.config.HttpRequestDispatcher;

import java.io.IOException;

public class HelloWorldDispatcher implements HttpRequestDispatcher {
    @Override
    public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
        response.setBody("<h1>Hello world</h1>");
    }
}
