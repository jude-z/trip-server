package com.tripplannerai.advice;

import com.tripplannerai.common.exception.member.*;
import com.tripplannerai.controller.member.MemberController;
import com.tripplannerai.dto.response.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import static com.tripplannerai.util.ConstClass.*;
@Order(1)
@RestControllerAdvice(basePackageClasses = MemberController.class)
public class MemberAdviceController {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_MEMBER_CODE,NOT_FOUND_MEMBER_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UnCorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleUnCorrectPasswordException() {
        return new ResponseEntity<>(ErrorResponse.of(UN_CORRECT_PASSWORD_CODE,UN_CORRECT_PASSWORD_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotAuthorizeException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizeException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_AUTHORIZED_CODE,NOT_AUTHORIZED_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MemberExistException.class)
    public ResponseEntity<ErrorResponse> handleMemberExistException() {
        return new ResponseEntity<>(ErrorResponse.of(MEMBER_EXISTS_CODE,MEMBER_EXISTS_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundCertificationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundCertificationException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_CERTIFICATION_CODE,NOT_FOUND_CERTIFICATION_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotCorrectCertificationException.class)
    public ResponseEntity<ErrorResponse> handleNotCorrectCertificationException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_CORRECT_CERTIFICATION_CODE,NOT_CORRECT_CERTIFICATION_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwtTokenException() {
        return new ResponseEntity<>(ErrorResponse.of(INVALID_JWT_TOKEN_CODE,INVALID_JWT_TOKEN_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException() {
        return new ResponseEntity<>(ErrorResponse.of(UPLOAD_FAILED_CODE,UPLOAD_FAILED_MESSAGE), HttpStatus.BAD_REQUEST);
    }
}
