package server.dine.pizza.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Duration;
import java.time.Instant;


@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oven {

    @JsonIgnore
    private long id;

    private int bakingTime;

    @JsonIgnore
    private Pizza pizza;

    @JsonIgnore
    private boolean busy;

    @JsonIgnore
    private Instant startBake;

    @JsonIgnore
    private Order order;

    public Oven(int bakingTime) {
        this.bakingTime = bakingTime;
        this.pizza = null;
    }

    @Override
    public String toString() {
        String pizzaString = "Not busy";
        if (this.pizza != null) {
            pizzaString = this.pizza.toString();
        }
        return "Oven [id=" + this.id + ", baking time= " + this.bakingTime + ", current baking pizza= " + pizzaString + "]";
    }

    public void bakePizza(Pizza pizza) {
        this.pizza = pizza;
        this.busy = true;
        //set current instant
        this.startBake = Instant.now();
    }

    @JsonIgnore
    public boolean isDone() {
        // bake time elapsed?
        Instant finishBake = Instant.now();
        return Duration.between(startBake, finishBake).toMillis() / 1000 >= bakingTime;
    }

    public void release() {
        this.startBake = null;
        this.busy = false;
    }

    public Order getOrder() {
        return order;
    }
}
