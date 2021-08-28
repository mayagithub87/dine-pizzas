package client.dine.pizza.consumer;

import client.dine.pizza.domain.Order;
import client.dine.pizza.domain.Topping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client class that consumes server api.
 */
@Component
public class OrderClient {

    private String serverApiUrl;

    private final String orders_url = "orders";

    private RestTemplate restTemplate;

    public OrderClient(@Value("${dine-pizza-api-url}") String prop) {
        this.serverApiUrl = prop;

        //validating that url has trailing slash
        if (!prop.endsWith("/"))
            serverApiUrl = prop.concat("/");
        else
            serverApiUrl = prop;

        restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(new CustomRestErrorHandling());
    }

    public List<Topping> getToppins() {
        List<Topping> toppings = null;
        ResponseEntity<List<Topping>> responseEntity =
                restTemplate.exchange(serverApiUrl + orders_url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Topping>>() {
                });
        if (responseEntity.getStatusCode() == HttpStatus.OK)
            toppings = responseEntity.getBody();
        return toppings;
    }

    public Order addOrder(Order order) {
        Order newOrder = null;
        ResponseEntity<Order> responseEntity = restTemplate.postForEntity(serverApiUrl + orders_url, order, Order.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK)
            newOrder = responseEntity.getBody();
        return newOrder;
    }

}
