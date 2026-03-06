package com.tripplannerai.common.exception.course;

public class NotFoundCourseLikeException extends RuntimeException{
    public NotFoundCourseLikeException(){};
    public NotFoundCourseLikeException(String message) {
        super(message);}
    public NotFoundCourseLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCourseLikeException(Throwable cause) {
        super(cause);
    }

    public NotFoundCourseLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
