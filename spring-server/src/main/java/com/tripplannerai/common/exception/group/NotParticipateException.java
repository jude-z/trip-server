package com.tripplannerai.common.exception.group;

public class NotParticipateException extends RuntimeException {

    public NotParticipateException() {
    }

    public NotParticipateException(String message) {
        super(message);
    }

    public NotParticipateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotParticipateException(Throwable cause) {
        super(cause);
    }

    public NotParticipateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
