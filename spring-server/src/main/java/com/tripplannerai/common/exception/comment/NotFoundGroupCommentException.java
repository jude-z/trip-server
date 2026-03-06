package com.tripplannerai.common.exception.comment;

public class NotFoundGroupCommentException extends RuntimeException {

    public NotFoundGroupCommentException() {
    }

    public NotFoundGroupCommentException(String message) {
        super(message);
    }

    public NotFoundGroupCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundGroupCommentException(Throwable cause) {
        super(cause);
    }

    public NotFoundGroupCommentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
