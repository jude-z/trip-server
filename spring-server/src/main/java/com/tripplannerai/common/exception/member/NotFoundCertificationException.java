package com.tripplannerai.common.exception.member;

public class NotFoundCertificationException extends RuntimeException {
    public NotFoundCertificationException() {
    }

    public NotFoundCertificationException(String message) {
        super(message);
    }

    public NotFoundCertificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCertificationException(Throwable cause) {
        super(cause);
    }

    public NotFoundCertificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
