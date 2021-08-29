package client.dine.pizza.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Order {

    @JsonIgnore
    private long id;

    private String name;

    private List<Pizza> pizzas;

    public Order(String name) {
        this.name = name;
        this.pizzas = new ArrayList<Pizza>();
    }

    @Override
    public String toString() {
        final String[] pizzasString = {""};
        if (this.pizzas.size() > 0) {
            this.pizzas.forEach(pizza -> pizzasString[0] += "\n" + pizza.toString());
        }
        return "Order [client = " + this.name + ", pizzas = " + this.pizzas.size()
                + "]: " + pizzasString[0];
    }

}
