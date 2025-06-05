package com.basic.miniPjt5.exception;

public class TMDBApiException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public TMDBApiException(String message) {
        super(message);
        this.errorCode = "TMDB_API_ERROR";
        this.httpStatus = 503;
    }

    public TMDBApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TMDB_API_ERROR";
        this.httpStatus = 503;
    }

    public TMDBApiException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}