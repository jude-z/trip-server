package core.api.common.advice;

import core.api.response.ErrorResponse;
import core.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CommonAdviceController {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException exception) {
        log.error("exception!! ",exception);
        ErrorResponse errorResponse = ErrorResponse.of(exception.getStatus());
        return new ResponseEntity<>(errorResponse,exception.getStatus().getHttpStatus());
    }

}
