package server.dine.pizza.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.dine.pizza.service.DinePizzaService;

@Component
public class OvensManager {

    private final DinePizzaService dinePizzaService;

    @Value("${order-cron-delay}")
    private int cronDelay;

    public OvensManager(DinePizzaService dinePizzaService) {
        this.dinePizzaService = dinePizzaService;
    }

    /**
     * Cron that will check for ovens availability
     */
    @Scheduled(fixedRateString = "${order-cron-delay}")
    public void execute() {
        // are there busy ovens?
        if (dinePizzaService.hasBusyOvens())
            dinePizzaService.releaseOvens();
    }

    public long getDelay() {
        return cronDelay;
    }
}
