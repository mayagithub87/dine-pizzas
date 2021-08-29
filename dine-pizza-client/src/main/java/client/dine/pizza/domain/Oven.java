package client.dine.pizza.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter
@NoArgsConstructor
public class Oven {

    private long id;

    private Integer bakingTime;

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
        return "Oven [id=" + this.id + ", baking time= " + this.bakingClock + "/" + this.bakingTime + ", current baking order= " + bakingOrder + "]";
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
