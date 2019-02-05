package com.alex.http.exception;

public class HttpServerConfigException extends HttpServerException {
    private static final long serialVersionUID = -4137792456332485195L;

    public HttpServerConfigException(String message) {
        super(message);
    }

    public HttpServerConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServerConfigException(Throwable cause) {
        super(cause);
    }
}
