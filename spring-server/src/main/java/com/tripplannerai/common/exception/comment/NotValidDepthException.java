package com.tripplannerai.common.exception.comment;

public class NotValidDepthException extends RuntimeException {

    public NotValidDepthException() {
    }

    public NotValidDepthException(String message) {
        super(message);
    }

    public NotValidDepthException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidDepthException(Throwable cause) {
        super(cause);
    }

    public NotValidDepthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
