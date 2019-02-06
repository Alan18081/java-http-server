package com.alex.http.impl;

import com.alex.http.config.HttpServerConfig;

public abstract class AbstractHttpConfigurableComponent {
    protected DefaultHttpServerConfig httpServerConfig;

    protected AbstractHttpConfigurableComponent(DefaultHttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
    }

    protected DefaultHttpServerConfig getHttpServerConfig() {
        return httpServerConfig;
    }
}
