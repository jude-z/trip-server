package com.tripplannerai.common.exception.destination;

public class NotFoundDDestinationException extends RuntimeException {
    public NotFoundDDestinationException() {
    }

    public NotFoundDDestinationException(String message) {
        super(message);
    }

    public NotFoundDDestinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundDDestinationException(Throwable cause) {
        super(cause);
    }

    public NotFoundDDestinationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
