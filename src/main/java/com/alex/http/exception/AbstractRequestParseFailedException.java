package com.alex.http.exception;

public abstract class AbstractRequestParseFailedException extends HttpServerException {
    private static final long serialVersionUID = 7930974998955486061L;
    private final String startingLine;

    public AbstractRequestParseFailedException(String message, String startingLine) {
        super(message);
        this.startingLine = startingLine;
    }

    public AbstractRequestParseFailedException(String message, Throwable cause, String startingLine) {
        super(message, cause);
        this.startingLine = startingLine;
    }

    public AbstractRequestParseFailedException(Throwable cause, String startingLine) {
        super(cause);
        this.startingLine = startingLine;
    }

    public String getStartingLine() {
        return startingLine;
    }
}
