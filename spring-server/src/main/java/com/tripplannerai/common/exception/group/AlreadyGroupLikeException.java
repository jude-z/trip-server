package com.tripplannerai.common.exception.group;

public class AlreadyGroupLikeException extends RuntimeException {

    public AlreadyGroupLikeException() {
    }

    public AlreadyGroupLikeException(String message) {
        super(message);
    }

    public AlreadyGroupLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyGroupLikeException(Throwable cause) {
        super(cause);
    }

    public AlreadyGroupLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
