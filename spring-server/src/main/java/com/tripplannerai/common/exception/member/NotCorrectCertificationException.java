package com.tripplannerai.common.exception.member;

public class NotCorrectCertificationException extends RuntimeException {
    public NotCorrectCertificationException() {
    }

    public NotCorrectCertificationException(String message) {
        super(message);
    }

    public NotCorrectCertificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCorrectCertificationException(Throwable cause) {
        super(cause);
    }

    public NotCorrectCertificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
