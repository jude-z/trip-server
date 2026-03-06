package com.tripplannerai.common.exception.group;

public class InvalidPointException extends RuntimeException {

    public InvalidPointException() {
    }

    public InvalidPointException(String message) {
        super(message);
    }

    public InvalidPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPointException(Throwable cause) {
        super(cause);
    }

    public InvalidPointException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
