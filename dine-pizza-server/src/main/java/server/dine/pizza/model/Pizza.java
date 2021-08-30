package server.dine.pizza.model;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Pizza {

    //    @Id
//    @Column(name = "id")
    private long id;

//   @Column(name = "order_id")     Only if DB
//   private long orderId;

    private List<Topping> toppings;

    public Pizza(long id, List<Topping> toppings) {
        this.id = id; // optional, create generator of Id autoincrement
        this.toppings = toppings;
    }

    public long getId() {
        return this.id;
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

