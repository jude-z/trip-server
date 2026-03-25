package com.mockserver.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<?> handleException(Exception e){
        log.error("error",e);
        return ResponseEntity.badRequest().build();

    }
}
