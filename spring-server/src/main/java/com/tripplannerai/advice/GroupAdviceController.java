package com.tripplannerai.advice;

import com.tripplannerai.controller.group.GroupController;
import com.tripplannerai.dto.response.ErrorResponse;
import com.tripplannerai.common.exception.group.AlreadyParticipateException;
import com.tripplannerai.common.exception.group.NotFoundGroupException;
import com.tripplannerai.common.exception.group.NotParticipateException;
import com.tripplannerai.common.exception.member.NotAuthorizeException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.*;

@Order(1)
@RestControllerAdvice(basePackageClasses = GroupController.class)
public class GroupAdviceController {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_MEMBER_CODE,NOT_FOUND_MEMBER_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotFoundGroupException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundGroupException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_GROUP_CODE,NOT_FOUND_GROUP_MESSAGE), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AlreadyParticipateException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyParticipateException() {
        return new ResponseEntity<>(ErrorResponse.of(ALREADY_PARTICIPATE_GROUP_CODE,ALREADY_PARTICIPATE_GROUP_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotParticipateException.class)
    public ResponseEntity<ErrorResponse> handleNotParticipateException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_PARTICIPATE_GROUP_CODE,NOT_PARTICIPATE_GROUP_MESSAGE), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotAuthorizeException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizeException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_AUTHORIZED_CODE,NOT_AUTHORIZED_MESSAGE), HttpStatus.BAD_REQUEST);
    }

}
