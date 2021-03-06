package com.alex.http.config;

import com.alex.http.HttpServerContext;
import com.alex.http.ServerInfo;
import org.apache.commons.dbcp2.BasicDataSource;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public interface HttpServerConfig extends AutoCloseable {

    ServerInfo getServerInfo();

    String getStatusMessage(int statusCode);

    HttpRequestParser getHttpRequestParser();

    HttpResponseBuilder getHttpResponseBuilder();

    HttpResponseWriter getHttpResponseWriter();

    HttpServerContext getHttpServerContext();

    HttpRequestDispatcher getHttpRequestDispatcher();

    ThreadFactory getWorkersThreadFactory();

    HttpClientSocketHandler buildHttpClientSocketHandler(Socket socket);

}
