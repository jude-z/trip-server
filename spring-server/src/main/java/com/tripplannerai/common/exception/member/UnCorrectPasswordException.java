package com.tripplannerai.common.exception.member;

public class UnCorrectPasswordException extends RuntimeException {

    public UnCorrectPasswordException() {
    }

    public UnCorrectPasswordException(String message) {
        super(message);
    }

    public UnCorrectPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnCorrectPasswordException(Throwable cause) {
        super(cause);
    }

    public UnCorrectPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
