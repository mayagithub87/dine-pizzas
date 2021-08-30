package server.dine.pizza.core.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String exceptionDetail) {
        super(exceptionDetail);
    }
}
