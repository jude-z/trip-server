package com.tripplannerai.common.exception.group;

public class NotFoundGroupException extends RuntimeException {
    public NotFoundGroupException() {
    }

    public NotFoundGroupException(String message) {
        super(message);
    }

    public NotFoundGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundGroupException(Throwable cause) {
        super(cause);
    }

    public NotFoundGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
