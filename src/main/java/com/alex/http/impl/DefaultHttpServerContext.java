package com.alex.http.impl;

import com.alex.http.Constants;
import com.alex.http.HtmlTemplateManager;
import com.alex.http.HttpServerContext;
import com.alex.http.ServerInfo;
import com.alex.http.exception.HttpServerConfigException;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

public class DefaultHttpServerContext extends AbstractHttpConfigurableComponent implements HttpServerContext {
    DefaultHttpServerContext(DefaultHttpServerConfig httpServerConfig) {
        super(httpServerConfig);
    }

    @Override
    public ServerInfo getServerInfo() {
        return getHttpServerConfig().getServerInfo();
    }

    @Override
    public Collection<String> getSupportedRequestMethods() {
        return Constants.ALLOWED_METHODS;
    }

    @Override
    public Properties getSupportedResponseStatuses() {
        Properties properties = new Properties();
        properties.putAll(getHttpServerConfig().getStatusesProperties());
        return properties;
    }

    @Override
    public DataSource getDataSource() {
        if(getHttpServerConfig().getBasicDataSource() != null) {
            return getHttpServerConfig().getBasicDataSource();
        } else {
            throw new HttpServerConfigException("Datasource is not configured for this context");
        }
    }

    @Override
    public Path getRootPath() {
        return getHttpServerConfig().getRootPath();
    }

    @Override
    public String getContentType(String extension) {
        String result = getHttpServerConfig().getMimeTypesProperties().getProperty(extension);
        return result != null ? result : "text/plain";
    }

    @Override
    public HtmlTemplateManager getHtmlTemplateManager() {
        return getHttpServerConfig().getHtmlTemplateManager();
    }

    @Override
    public Integer getExpiresDaysForResource(String extension) {
        return getHttpServerConfig().getStaticExpiresDays();
    }
}
