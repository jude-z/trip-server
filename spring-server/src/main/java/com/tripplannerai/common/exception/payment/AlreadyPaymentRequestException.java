package com.tripplannerai.common.exception.payment;

public class AlreadyPaymentRequestException extends RuntimeException {

    public AlreadyPaymentRequestException() {
    }

    public AlreadyPaymentRequestException(String message) {
        super(message);
    }

    public AlreadyPaymentRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyPaymentRequestException(Throwable cause) {
        super(cause);
    }

    public AlreadyPaymentRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
