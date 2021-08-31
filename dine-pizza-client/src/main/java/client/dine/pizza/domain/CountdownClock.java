package client.dine.pizza.domain;

import org.springframework.util.StopWatch;

public class CountdownClock {

    public CountdownClock(int start, String prefix, String customer) {
        StopWatch clock = new StopWatch("Countdown-clock");
        clock.start("Countdown-clock-th");
        for (int i = 1; i <= start; i++) {
            try {
                System.out.printf("%sCustomer: %s [Order Baking Timer: %d/%d seconds]", prefix, customer, i, start);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clock.stop();
    }
}
