package com.alex.http.impl;

import com.alex.http.HttpServer;
import com.alex.http.config.HttpServerConfig;
import com.alex.http.exception.HttpServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DefaultHttpServer implements HttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServer.class);
    private final HttpServerConfig httpServerConfig;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Thread mainServerThread;
    private volatile boolean serverStopped;

    protected DefaultHttpServer(HttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
        this.executorService = createExecutorService();
        this.mainServerThread = createMainThread(createServerRunnable());
        this.serverSocket = createServerSocket();
        this.serverStopped = false;
    }

    @Override
    public void start() {
        if(mainServerThread.getState() != Thread.State.NEW) {
            throw new HttpServerException("Current web server already started and stopped! Please, create a new http server instance");
        }
        Runtime.getRuntime().addShutdownHook(getShutdownHook());
        mainServerThread.start();
        LOGGER.info("Server started: " + httpServerConfig.getServerInfo());
    }

    @Override
    public void stop() {
        LOGGER.info("Detected stop cmd");
        mainServerThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("Error during close server socket: " + e.getMessage(), e);
        }
    }

    private ServerSocket createServerSocket() {
        int port = httpServerConfig.getServerInfo().getPort();
        System.out.println(port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            return serverSocket;
        } catch (IOException e) {
            throw new HttpServerException("Can't create server socket with port = " + port, e);
        }
    }

    private ExecutorService createExecutorService() {
        ThreadFactory threadFactory = httpServerConfig.getWorkersThreadFactory();
        int threadCount = httpServerConfig.getServerInfo().getThreadCount();
        if(threadCount > 0) {
            return Executors.newFixedThreadPool(threadCount, threadFactory);
        } else {
            return Executors.newCachedThreadPool(threadFactory);
        }
    }

    private Thread createMainThread(Runnable r) {
        Thread thread = new Thread(r, "Main server thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(false);
        return thread;
    }

    private Runnable createServerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while(!mainServerThread.isInterrupted()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        executorService.submit(httpServerConfig.buildHttpClientSocketHandler(clientSocket));
                    } catch (IOException e) {
                        if(!serverSocket.isClosed()) {
                            LOGGER.error("Can't accept client socket: " + e.getMessage(), e);
                        }
                        destroyHttpServer();
                        break;
                    }
                }
                System.exit(0);
            }
        };
    }

    private Thread getShutdownHook() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                if(!serverStopped) {
                    destroyHttpServer();
                }
            }
        });
    }

    private void destroyHttpServer() {
        try {
            httpServerConfig.close();
        } catch (Exception e) {
            LOGGER.error("Close httpServerConfig failed: " + e.getMessage(), e);
        }
        executorService.shutdownNow();
        LOGGER.info("Server stopped");
        serverStopped = true;
    }
}
