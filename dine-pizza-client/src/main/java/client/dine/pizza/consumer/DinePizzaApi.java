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
public class DinePizzaApi {

    private String serverApiUrl;

    private RestTemplate restTemplate;

    public DinePizzaApi(@Value("${dine-pizza-api-url}") String prop) {
        this.serverApiUrl = prop;
        //validating that url has trailing slash
        serverApiUrl = (!prop.endsWith("/"))? prop.concat("/") : prop;
        restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(new CustomRestErrorHandling());
    }

    public List<Topping> getToppins() {
        String endpoint = "orders";
        List<Topping> toppings = null;
        ResponseEntity<List<Topping>> responseEntity =
                restTemplate.exchange(serverApiUrl + endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<Topping>>() {
                });
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            toppings = responseEntity.getBody();
        }
        return toppings;
    }

    public Order addOrder(Order order) {
        String endpoint = "orders";
        Order newOrder = null;
        ResponseEntity<Order> responseEntity = restTemplate.postForEntity(serverApiUrl + endpoint, order, Order.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            newOrder = responseEntity.getBody();
        }
        return newOrder;
    }

    public List<Order> getOrders() {
        String endpoint = "orders/status";
        List<Order> orders = null;
        ResponseEntity<List<Order>> responseEntity =
                restTemplate.exchange(serverApiUrl + endpoint, HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {
                });
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            orders = responseEntity.getBody();
        }
        return orders;
    }

}
