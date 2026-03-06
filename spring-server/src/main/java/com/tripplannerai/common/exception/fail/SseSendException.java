package com.tripplannerai.common.exception.fail;

public class SseSendException extends RuntimeException {

    public SseSendException() {
    }

    public SseSendException(String message) {
        super(message);
    }

    public SseSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public SseSendException(Throwable cause) {
        super(cause);
    }

    public SseSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
