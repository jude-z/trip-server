package com.tripplannerai.advice;

import com.tripplannerai.controller.fail.FailController;
import com.tripplannerai.dto.response.ErrorResponse;
import com.tripplannerai.common.exception.fail.SseSendException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.SSE_SEND_ERROR_CODE;
import static com.tripplannerai.util.ConstClass.SSE_SEND_ERROR_MESSAGE;

@Order(1)
@RestControllerAdvice(basePackageClasses = FailController.class)
public class FailAdviceController {
    @ExceptionHandler(SseSendException.class)
    public ResponseEntity<ErrorResponse> handleSseSendException(){
        return new ResponseEntity<>(ErrorResponse.of(SSE_SEND_ERROR_CODE, SSE_SEND_ERROR_MESSAGE), HttpStatus.BAD_REQUEST);
    }

}
