package server.dine.pizza.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Topping {

    //    @Id
//    @Column(name = "id")
    private long id;

    //    @Column(name = "name")
    private String name;

    //    @Column(name = "quantity")
    private int quantity;

    public Topping(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Topping(long id, String name, int quantity) {
        this.id = id; // optional, create generator of Id autoincrement
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Topping [name=" + this.name + ", quantity=" + this.quantity + "]";
    }

}