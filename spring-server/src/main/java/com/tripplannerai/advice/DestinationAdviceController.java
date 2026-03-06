package com.tripplannerai.advice;

import com.tripplannerai.dto.response.ErrorResponse;
import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.*;

@Order(1)
@RestControllerAdvice(basePackageClasses = DestinationAdviceController.class)
public class DestinationAdviceController {
    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_MEMBER_CODE,NOT_FOUND_MEMBER_MESSAGE), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundDDestinationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundDDestinationException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_DESTINATION_CODE,NOT_FOUND_DESTINATION_MESSAGE), HttpStatus.NOT_FOUND);
    }

}
