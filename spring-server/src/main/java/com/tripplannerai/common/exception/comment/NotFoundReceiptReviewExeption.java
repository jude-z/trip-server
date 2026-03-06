package com.tripplannerai.common.exception.comment;

public class NotFoundReceiptReviewExeption extends RuntimeException{

    public NotFoundReceiptReviewExeption() {
    }

    public NotFoundReceiptReviewExeption(String message) {
        super(message);
    }

    public NotFoundReceiptReviewExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundReceiptReviewExeption(Throwable cause) {
        super(cause);
    }

    public NotFoundReceiptReviewExeption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
