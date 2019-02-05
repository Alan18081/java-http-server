package com.alex.http.impl;

import com.alex.http.HtmlTemplateManager;
import com.alex.http.HttpServerContext;
import com.alex.http.ServerInfo;
import com.alex.http.config.*;
import com.alex.http.exception.HttpServerConfigException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

public class DefaultHttpServerConfig implements HttpServerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServerConfig.class);

    private final Properties serverProperties = new Properties();
    private final Properties statusesProperties = new Properties();
    private final Properties mimeTypesProperties = new Properties();
    private final BasicDataSource basicDataSource;
    private final Path rootPath;
    private final HttpServerContext httpServerContext;
    private final HttpRequestParser httpRequestParser;
    private final HttpResponseWriter httpResponseWriter;
    private final HttpResponseBuilder httpResponseBuilder;
    private final HttpRequestDispatcher httpRequestDispatcher;
    private final ThreadFactory workerThreadFactory;
    private final HtmlTemplateManager htmlTemplateManager;
    private final ServerInfo serverInfo;
    private final List<String> staticExpiresExtensions;
    private final int staticExpiresDays;

    DefaultHttpServerConfig(Properties overrideServerProperties) {
        loadAllProperties(overrideServerProperties);
        this.rootPath = createRootPath();
        this.basicDataSource = createBasicDataSource();
    }

    private void loadAllProperties(Properties overrideServerProperties) {
        ClassLoader classLoader = DefaultHttpServerConfig.class.getClassLoader();
        loadProperties(this.statusesProperties, classLoader, "statuses.properties");
        loadProperties(this.mimeTypesProperties, classLoader, "mime-types.properties");
        loadProperties(this.serverProperties, classLoader, "server.properties");
        if(overrideServerProperties != null) {
            LOGGER.info("Overrides default server properties");
            this.serverProperties.putAll(overrideServerProperties);
        }
        logServerProperties();
    }

    private void loadProperties(Properties properties, ClassLoader classLoader, String resource) {
        try(InputStream in = classLoader.getResourceAsStream(resource)) {
            if(in != null) {
                properties.load(in);
                LOGGER.debug("Successfully loaded properties from classpath resource: {}", resource);
            }
        } catch (IOException e) {
            throw new HttpServerConfigException("Can't load properties from resource: " + resource, e);
        }
    }

    private void logServerProperties() {
        if(LOGGER.isDebugEnabled()) {
            StringBuilder res = new StringBuilder("Current server properties is:\n");
            for(Map.Entry<Object, Object> entry : this.serverProperties.entrySet()) {
                res.append(entry.getKey()).append('=').append(entry.getValue()).append('=');
            }
            LOGGER.debug(res.toString());
        }
    }

    private Path createRootPath() {
        Path path = Paths.get(new File(this.serverProperties.getProperty("webapp.static.dir.root")).getAbsoluteFile().toURI());
        if(!Files.exists(path)) {
            throw new HttpServerConfigException("Root path not found: " + path.toString());
        }
        if(!Files.isDirectory(path)) {
            throw new HttpServerConfigException("Root path is not directory: " + path.toString());
        }
        LOGGER.info("Root path is {}", path.toAbsolutePath());
        return path;
    }

    private Path createBasicDataSource() {
        BasicDataSource ds = null;
        if(Boolean.parseBoolean(serverProperties.getProperty("db.datasource.enabled"))) {
            ds = new BasicDataSource();
            ds.setDefaultAutoCommit(false);
            ds.setRollbackOnReturn(true);
            ds.setDriverClassName(Objects.requireNonNull(serverProperties.getProperty("db.datasource.driver")));
            ds.setUrl(Objects.requireNonNull(serverProperties.get));
        }
    }

    @Override
    public ServerInfo getServerInfo() {
        return null;
    }

    @Override
    public String getStatusMessage(int statusCode) {
        return null;
    }

    @Override
    public HttpRequestParser getHttpRequestParser() {
        return null;
    }

    @Override
    public HttpResponseBuilder getHttpResponseBuilder() {
        return null;
    }

    @Override
    public HttpResponseWriter getHttpResponseWriter() {
        return null;
    }

    @Override
    public HttpServerContext getHttpServerContext() {
        return null;
    }

    @Override
    public HttpRequestDispatcher getHttpRequestDispatcher() {
        return null;
    }

    @Override
    public ThreadFactory getWorkersThreadFactory() {
        return null;
    }

    @Override
    public HttpClientSocketHandler buildHttpClientSocketHandler() {
        return null;
    }
}
