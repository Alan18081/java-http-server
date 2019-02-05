package com.alex.http.exception;

import com.alex.http.Constants;

public class MethodNotAllowedException extends AbstractRequestParseFailedException {
    public MethodNotAllowedException(String method, String startingLine) {
        super("Only " + Constants.ALLOWED_METHODS + " is supported. Current method is " + method, startingLine);
        setStatusCode(405);
    }
}
