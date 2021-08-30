package server.dine.pizza.core.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NotValidException extends RuntimeException {
    public NotValidException(String exceptionDetail) {
        super(exceptionDetail);
    }
}
