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
    private Order order;

    @JsonIgnore
    private boolean busy;

    @JsonIgnore
    private Instant startBake;

    public Oven(int bakingTime) {
        this.bakingTime = bakingTime;
        this.order = null;
    }

    @Override
    public String toString() {
        String orderString = "No order processing";
        if (this.order != null) {
            orderString = this.order.toString();
        }
        return "Oven [id=" + this.id + ", baking time= " + this.bakingTime + ", current baking order= " + orderString + "]";
    }

    public void bakeOrder(Order order) {
        this.order = order;
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
}
