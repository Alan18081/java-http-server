package com.alex.http.exception;

public class HttpVersionNotSupportedException extends AbstractRequestParseFailedException {
    private static final long serialVersionUID = -4204131141345512747L;

    public HttpVersionNotSupportedException(String message, String startingLine) {
        super(message, startingLine);
        setStatusCode(505);
    }
}
