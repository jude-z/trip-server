package com.tripplannerai.common.exception.comment;

public class NotFoundCommentLikeException extends RuntimeException {

    public NotFoundCommentLikeException() {
    }

    public NotFoundCommentLikeException(String message) {
        super(message);
    }

    public NotFoundCommentLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCommentLikeException(Throwable cause) {
        super(cause);
    }

    public NotFoundCommentLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
