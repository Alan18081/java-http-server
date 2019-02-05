package com.alex.http.impl;

import com.alex.http.config.HttpServerConfig;

public abstract class AbstractHttpConfigurableComponent {
    protected HttpServerConfig httpServerConfig;

    protected AbstractHttpConfigurableComponent(HttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
    }
}
