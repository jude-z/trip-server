package com.tripplannerai.advice;

import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.group.AlreadyParticipateException;
import com.tripplannerai.common.exception.group.NotFoundGroupException;
import com.tripplannerai.common.exception.group.NotParticipateException;
import com.tripplannerai.common.exception.keep.NotFoundKeepException;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.controller.group.GroupController;
import com.tripplannerai.controller.keep.KeepController;
import com.tripplannerai.dto.response.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.*;

@Order(1)
@RestControllerAdvice(basePackageClasses = KeepController.class)
public class KeepAdviceController {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_MEMBER_CODE,NOT_FOUND_MEMBER_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotFoundDDestinationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundDDestinationException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_DESTINATION_CODE,NOT_FOUND_DESTINATION_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotFoundKeepException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundKeepException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_KEEP_CODE,NOT_FOUND_KEEP_MESSAGE),HttpStatus.NOT_FOUND);
    }

}
