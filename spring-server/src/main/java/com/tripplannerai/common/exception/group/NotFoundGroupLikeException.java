package com.tripplannerai.common.exception.group;

public class NotFoundGroupLikeException extends RuntimeException {
    public NotFoundGroupLikeException() {
    }

    public NotFoundGroupLikeException(String message) {
        super(message);
    }

    public NotFoundGroupLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundGroupLikeException(Throwable cause) {
        super(cause);
    }

    public NotFoundGroupLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
