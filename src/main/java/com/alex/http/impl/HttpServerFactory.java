package com.alex.http.impl;

import com.alex.http.HttpServer;
import com.alex.http.config.HttpServerConfig;

import java.util.Properties;

public class HttpServerFactory {

    protected HttpServerFactory() {}

    public static HttpServerFactory create() {
        return new HttpServerFactory();
    }

    public HttpServer createHttpServer(Properties overridesServerProperties) {
        HttpServerConfig httpServerConfig = new DefaultHttpServerConfig(overridesServerProperties);
        return new DefaultHttpServer(httpServerConfig);
    }

}
