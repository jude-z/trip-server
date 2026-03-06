package com.tripplannerai.common.exception.course;

public class NotFoundCourseException extends RuntimeException{
    public NotFoundCourseException(){};
    public NotFoundCourseException(String message) {
        super(message);}
    public NotFoundCourseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundCourseException(Throwable cause) {
        super(cause);
    }

    public NotFoundCourseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
