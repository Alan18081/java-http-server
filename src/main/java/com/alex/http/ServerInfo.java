package com.alex.http;

public class ServerInfo {
    private String name;
    private int port;
    private int threadCount;

    public ServerInfo(String name, int port, int threadCount) {
        this.name = name;
        this.port = port;
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                ", port=" + port +
                ", threadCount=" + threadCount +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
