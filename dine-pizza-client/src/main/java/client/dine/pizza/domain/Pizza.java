package client.dine.pizza.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Pizza {

    @JsonIgnore
    private long id;

    private List<Topping> toppings;

    public Pizza(List<Topping> toppings) {
        //this.id = id; // optional, create generator of Id autoincrement
        this.toppings = toppings;
    }

    @Override
    public String toString() {
        final String[] toppingsString = {""};
        if (this.toppings.size() > 0) {
            this.toppings.forEach(topping -> toppingsString[0] += "\n . " + topping.toString());
        }
        return "Pizza [toppings = " + this.toppings.size() + " ]: " + toppingsString[0];
    }

}

