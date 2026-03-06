package com.tripplannerai.common.exception.payment;

public class NotFoundTempPaymentException extends RuntimeException{

    public NotFoundTempPaymentException() {
    }

    public NotFoundTempPaymentException(String message) {
        super(message);
    }

    public NotFoundTempPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundTempPaymentException(Throwable cause) {
        super(cause);
    }

    public NotFoundTempPaymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
