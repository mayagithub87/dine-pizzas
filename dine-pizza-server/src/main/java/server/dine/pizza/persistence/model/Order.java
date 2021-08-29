package server.dine.pizza.persistence.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Order {

    //    @Id
//    @Column(name = "id")
    private long id;

    //    @Column(name = "name")
    private String customerName;

    private List<Pizza> pizzas;

    @Override
    public String toString() {
        final String[] pizzasString = {""};
        if (this.pizzas.size() > 0) {
            this.pizzas.forEach(pizza -> pizzasString[0] += "\n" + pizza.toString());
        }
        return "Order [id=" + this.id + ", client= " + this.customerName + ", pizzas= " + this.pizzas.size()
                + "] \n" + pizzasString[0];
    }

}
