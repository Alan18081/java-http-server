package com.alex.http.impl;

import com.alex.http.HttpRequest;
import com.alex.http.HttpResponse;
import com.alex.http.HttpServerContext;
import com.alex.http.config.HttpRequestDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class DefaultHttpHandler implements HttpRequestDispatcher {
    @Override
    public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
        String url = request.getUri();
        Path path = Paths.get(context.getRootPath().toString() + url);
        if(Files.exists(path)) {
            if(Files.isDirectory(path)) {
                handleDirectoryUrl(context, response, path);
            } else {
                handleFileUrl(context, response, path);
            }
        } else {
            response.setStatus(404);
            response.setBody("<h1>Not found</h1>");
        }
    }

    private void handleDirectoryUrl(HttpServerContext context, HttpResponse response, Path path) {
        String content = getResponseForDirectory();
    }

    private void handleFileUrl(HttpServerContext context, HttpResponse response, Path path) {

    }
}
