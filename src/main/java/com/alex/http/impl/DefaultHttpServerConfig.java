package com.alex.http.impl;

import com.alex.http.HtmlTemplateManager;
import com.alex.http.HttpServerContext;
import com.alex.http.ServerInfo;
import com.alex.http.config.*;
import com.alex.http.exception.DefaultHttpRequestDispatcher;
import com.alex.http.exception.HttpServerConfigException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
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

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    DefaultHttpServerConfig(Properties overrideServerProperties) {
        loadAllProperties(overrideServerProperties);
        this.rootPath = createRootPath();
        this.basicDataSource = createBasicDataSource();
        this.serverInfo = createServerInfo();
        this.staticExpiresDays = Integer.parseInt(this.serverProperties.getProperty("webapp.static.expires.days"));
        this.staticExpiresExtensions = Arrays.asList(this.serverProperties.getProperty("webapp.static.expires.extensions").split(","));

        this.httpServerContext = new DefaultHttpServerContext(this);
        this.httpRequestParser = new DefaultHttpRequestParser();
        this.httpResponseWriter = new DefaultHttpResponseWriter(this);
        this.httpResponseBuilder = new DefaultHttpResponseBuilder(this);
        this.httpRequestDispatcher = new DefaultHttpRequestDispatcher();
        this.workerThreadFactory = new DefaultThreadFactory();
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

    private BasicDataSource createBasicDataSource() {
        BasicDataSource ds = null;
        if(Boolean.parseBoolean(serverProperties.getProperty("db.datasource.enabled"))) {
            ds = new BasicDataSource();
            ds.setDefaultAutoCommit(false);
            ds.setRollbackOnReturn(true);
            ds.setDriverClassName(Objects.requireNonNull(serverProperties.getProperty("db.datasource.driver")));
            ds.setUrl(Objects.requireNonNull(serverProperties.getProperty("db.datasource.url")));
            ds.setUsername(Objects.requireNonNull(serverProperties.getProperty("db.datasource.username")));
            ds.setPassword(Objects.requireNonNull(serverProperties.getProperty("db.datasource.password")));
            ds.setInitialSize(Integer.parseInt(Objects.requireNonNull(serverProperties.getProperty("db.datasource.pool.initSize"))));
            ds.setMaxTotal(Integer.parseInt(Objects.requireNonNull(serverProperties.getProperty("db.datasource.pool.maxSize"))));
            LOGGER.info("Datasource is enabled. JDBC url is {}", ds.getUrl());
        } else {
            LOGGER.info("Datasource is disabled");
        }
        return ds;
    }

    private ServerInfo createServerInfo() {
        ServerInfo si = new ServerInfo(
          serverProperties.getProperty("server.name"),
          Integer.parseInt(serverProperties.getProperty("server.port")),
          Integer.parseInt(serverProperties.getProperty("server.thread.count"))
        );
        if(si.getThreadCount() <= 0) {
            throw new HttpServerConfigException("server.thread.count should be greater than zero");
        }
        return si;
    }

    @Override
    public ServerInfo getServerInfo() {
        return null;
    }

    @Override
    public String getStatusMessage(int statusCode) {
        String message = statusesProperties.getProperty(String.valueOf(statusCode));
        return message != null ? message : statusesProperties.getProperty("500");
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
    public HttpClientSocketHandler buildHttpClientSocketHandler(Socket socket) {
        return new DefaultHttpClientSocketHandler(socket, this);
    }

    @Override
    public void close() {
        if(basicDataSource != null) {
            try {
                basicDataSource.close();
            } catch (SQLException e) {
                LOGGER.error("Close datasource failed: " + e.getMessage(), e);
            }
        }
        LOGGER.info("DefaultHttpServerConfig is closed");
    }

    public Properties getServerProperties() {
        return serverProperties;
    }

    public Properties getStatusesProperties() {
        return statusesProperties;
    }

    public Properties getMimeTypesProperties() {
        return mimeTypesProperties;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public ThreadFactory getWorkerThreadFactory() {
        return workerThreadFactory;
    }

    public HtmlTemplateManager getHtmlTemplateManager() {
        return htmlTemplateManager;
    }

    public List<String> getStaticExpiresExtensions() {
        return staticExpiresExtensions;
    }

    public int getStaticExpiresDays() {
        return staticExpiresDays;
    }
}
