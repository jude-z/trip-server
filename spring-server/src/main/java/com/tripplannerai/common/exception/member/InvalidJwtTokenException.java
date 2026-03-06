package com.tripplannerai.common.exception.member;

public class InvalidJwtTokenException extends RuntimeException {

    public InvalidJwtTokenException() {
    }

    public InvalidJwtTokenException(String message) {
        super(message);
    }

    public InvalidJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJwtTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidJwtTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
