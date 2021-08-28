package client.dine.pizza.interactive;

import client.dine.pizza.consumer.OrderClient;
import client.dine.pizza.domain.Order;
import client.dine.pizza.domain.Topping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Entry point between customer (through command line) and server api.
 */
@Component
public class CustomerInterface {

    private OrderClient orderClient;

    public CustomerInterface(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public void printWelcomeMessage() {
        List<Topping> toppins = orderClient.getToppins();
        System.out.println("\n \n \n Welcome to Dine Pizza Service! \n These are the available toppings.\n");
        for (Topping t : toppins) {
            System.out.println(String.format("-%s-", t.getName()));
        }
    }

    public void addOrder() {
        //TODO retreive input and create order
        Order addedOrder = orderClient.addOrder(new Order());

    }

}
