package client.dine.pizza.consumer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomRestErrorHandling implements ResponseErrorHandler {

    private Logger logger = LoggerFactory.getLogger(CustomRestErrorHandling.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return (clientHttpResponse.getStatusCode().value() != 200);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        logger.error(clientHttpResponse.getStatusText());
    }
}
