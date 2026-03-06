package com.tripplannerai.common.exception.payment;

public class PaymentServerErrorException extends RuntimeException {

    public PaymentServerErrorException() {
    }

    public PaymentServerErrorException(String message) {
        super(message);
    }

    public PaymentServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentServerErrorException(Throwable cause) {
        super(cause);
    }

    public PaymentServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
