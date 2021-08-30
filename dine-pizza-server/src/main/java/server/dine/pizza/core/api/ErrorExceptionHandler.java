package server.dine.pizza.core.api;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import server.dine.pizza.core.api.exception.*;

import java.util.Date;

@Slf4j
@RestControllerAdvice
public class ErrorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorDetail> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log(ex);
        return getResponse(ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorDetail> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log(ex);
        return getResponse(ex.getMessage(), request.getDescription(false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public final ResponseEntity<ErrorDetail> handleAlreadyExistException(AlreadyExistException ex, WebRequest request) {
        log(ex);
        return getResponse(ex.getMessage(), request.getDescription(false), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorDetail> handleUserNotFoundException(RuntimeException ex, WebRequest request) {
        log(ex);
        return getResponse(ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetail> getResponse(String message, String details, HttpStatus status) {
        ErrorDetail errorDetails = new ErrorDetail(new Date(), message,
                details);
        return new ResponseEntity<>(errorDetails, status);
    }

    private void log(Exception e) {
        log.error("system error", e);
    }

    @Getter
    @ToString
    private static final class ErrorDetail {
        private Date timestamp;
        private String message;
        private String details;

        ErrorDetail(Date timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }
    }
}
