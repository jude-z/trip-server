package com.tripplannerai.common.exception.comment;

public class AlreadyCommentLikeException extends RuntimeException {
    public AlreadyCommentLikeException() {
    }

    public AlreadyCommentLikeException(String message) {
        super(message);
    }

    public AlreadyCommentLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyCommentLikeException(Throwable cause) {
        super(cause);
    }

    public AlreadyCommentLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
