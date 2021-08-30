package server.dine.pizza.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Order {

    private long id;

    private Status status;

    private String name;

    private List<Pizza> pizzas;

    @Override
    public String toString() {
        final String[] pizzasString = {""};
        if (this.pizzas.size() > 0) {
            this.pizzas.forEach(pizza -> pizzasString[0] += "\n" + pizza.toString());
        }
        return "Order [status=" + this.status + ", client= " + this.name + ", pizzas= " + this.pizzas.size()
                + "] " + pizzasString[0];
    }

}