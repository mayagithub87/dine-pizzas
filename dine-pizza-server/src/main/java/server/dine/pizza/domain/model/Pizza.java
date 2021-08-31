package server.dine.pizza.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Pizza {

    @JsonIgnore
    private long id;

    private List<Topping> toppings;

    public Pizza(long id, List<Topping> toppings) {
        this.id = id; // optional, create generator of Id autoincrement
        this.toppings = toppings;
    }

    public List<Topping> getToppings() {
        return this.toppings;
    }

    @Override
    public String toString() {
        final String[] toppingsString = {""};
        if (this.toppings.size() > 0) {
            this.toppings.forEach(topping -> toppingsString[0] += "\n" + topping.toString());
        }
        return "Pizza [id=" + this.id + ", toppings= " + this.toppings.size() + "] \n" + toppingsString[0];
    }

}

