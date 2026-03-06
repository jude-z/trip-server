package com.tripplannerai.common.exception.course;

public class AlreadyExistsReviewException extends IllegalStateException{
    public AlreadyExistsReviewException(){};
    public AlreadyExistsReviewException(String message) {
        super(message);}
    public AlreadyExistsReviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsReviewException(Throwable cause) {
        super(cause);
    }

}
