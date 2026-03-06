package com.tripplannerai.advice;


import com.tripplannerai.controller.festival.FestivalController;
import com.tripplannerai.dto.response.ErrorResponse;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.util.ConstClass;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Order(1)
@RestControllerAdvice(basePackageClasses = FestivalController.class)
public class FestivalAdviceController {

    @ExceptionHandler(NotAuthorizeException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizeException() {
        return new ResponseEntity<>(ErrorResponse.of(ConstClass.NOT_AUTHORIZED_CODE, ConstClass.NOT_AUTHORIZED_MESSAGE), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> failFestivalLikeHandler() {
        return new ResponseEntity<>(ErrorResponse.of("FA", "Fail"), HttpStatus.BAD_REQUEST);
    }



}
