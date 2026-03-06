package com.tripplannerai.common.exception.group;

public class AlreadyParticipateException extends RuntimeException {

    public AlreadyParticipateException() {
    }

    public AlreadyParticipateException(String message) {
        super(message);
    }

    public AlreadyParticipateException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyParticipateException(Throwable cause) {
        super(cause);
    }

    public AlreadyParticipateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
