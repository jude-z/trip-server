package com.tripplannerai.advice;

import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.controller.payment.PaymentController;
import com.tripplannerai.dto.response.ErrorResponse;
import com.tripplannerai.common.exception.payment.AlreadyPaymentRequestException;
import com.tripplannerai.common.exception.payment.NotFoundTempPaymentException;
import com.tripplannerai.common.exception.payment.PaymentServerErrorException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.*;

@Order(1)
@RestControllerAdvice(basePackageClasses = PaymentController.class)
public class PaymentAdviceController {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_MEMBER_CODE,NOT_FOUND_MEMBER_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AlreadyPaymentRequestException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyPaymentRequestException(){
        return new ResponseEntity<>(ErrorResponse.of(ALREADY_PAYMENT_REQUEST_CODE,ALREADY_PAYMENT_REQUEST_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PaymentServerErrorException.class)
    public ResponseEntity<ErrorResponse> handlePaymentServerErrorException(){
        return new ResponseEntity<>(ErrorResponse.of(PAYMENT_SERVER_ERROR_CODE,PAYMENT_SERVER_ERROR_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundTempPaymentException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundTempPaymentException(){
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_TEMP_PAYMENT_CODE,NOT_FOUND_TEMP_PAYMENT_MESSAGE), HttpStatus.BAD_REQUEST);
    }
}
