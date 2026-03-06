package com.tripplannerai.common.exception.member;

public class MemberExistException extends RuntimeException {
    public MemberExistException() {
    }

    public MemberExistException(String message) {
        super(message);
    }

    public MemberExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberExistException(Throwable cause) {
        super(cause);
    }

    public MemberExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
