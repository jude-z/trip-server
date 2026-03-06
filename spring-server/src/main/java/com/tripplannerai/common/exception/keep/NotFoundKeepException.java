package com.tripplannerai.common.exception.keep;


public class NotFoundKeepException extends RuntimeException {
    public NotFoundKeepException() {
    }

    public NotFoundKeepException(String message) {
        super(message);
    }

    public NotFoundKeepException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundKeepException(Throwable cause) {
        super(cause);
    }

    public NotFoundKeepException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
