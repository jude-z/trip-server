package com.tripplannerai.common.exception.payment;

public class NotFoundPaymentException extends  RuntimeException {

    public NotFoundPaymentException() {
    }

    public NotFoundPaymentException(String message) {
        super(message);
    }

    public NotFoundPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundPaymentException(Throwable cause) {
        super(cause);
    }

    public NotFoundPaymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
