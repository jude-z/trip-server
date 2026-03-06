package com.tripplannerai.advice;

import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.controller.coment.CommentController;
import com.tripplannerai.dto.response.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tripplannerai.util.ConstClass.*;

@Order(1)
@RestControllerAdvice(basePackageClasses = AiRecommendAdviceController.class)
public class AiRecommendAdviceController {
    @ExceptionHandler(NotFoundDDestinationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundDestinationException() {
        return new ResponseEntity<>(ErrorResponse.of(NOT_FOUND_DESTINATION_CODE,NOT_FOUND_DESTINATION_MESSAGE), HttpStatus.NOT_FOUND);
    }
}
