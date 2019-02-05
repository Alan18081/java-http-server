package com.alex.http.exception;

public class HttpServerException extends RuntimeException {
    private static final long serialVersionUID = 219693711791849018L;
    private int statusCode = 500;

    public HttpServerException(String message) {
        super(message);
    }

    public HttpServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServerException(Throwable cause) {
        super(cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
