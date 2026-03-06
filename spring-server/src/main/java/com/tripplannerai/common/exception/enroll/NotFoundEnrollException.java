package com.tripplannerai.common.exception.enroll;

public class NotFoundEnrollException extends RuntimeException {

    public NotFoundEnrollException() {
    }

    public NotFoundEnrollException(String message) {
        super(message);
    }

    public NotFoundEnrollException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundEnrollException(Throwable cause) {
        super(cause);
    }

    public NotFoundEnrollException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
