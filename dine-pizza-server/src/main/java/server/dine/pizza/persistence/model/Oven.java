package server.dine.pizza.persistence.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Oven {

    //    @Id
//    @Column(name = "id")
    private long id;

    //    @Column(name = "baking_time")
    private Integer bakingTime;

    //    @Column(name = "baking_clock")
    private int bakingClock;

    private Order bakingOrder;

    private boolean busy;

    public Oven(int bakingTime) {
        this.bakingTime = bakingTime;
        this.bakingClock = 0;
        this.bakingOrder = null;
    }

    public void countDownBakingClock() {
        if (this.bakingClock > 0) {
            this.bakingClock--;
        }
    }

    @Override
    public String toString() {
        String orderString = "No order processing";
        if (this.bakingOrder != null) {
            orderString = this.bakingOrder.toString();
        }
        return "Oven [id=" + this.id + ", baking time= " + this.bakingClock + "/" + this.bakingTime + ", current baking order= " + orderString + "]";
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
