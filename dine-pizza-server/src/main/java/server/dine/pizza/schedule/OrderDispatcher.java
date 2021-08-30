package server.dine.pizza.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.dine.pizza.service.DinePizzaService;

@Component
public class OrderDispatcher {

    private final DinePizzaService dinePizzaService;

    @Value("${order-cron-delay}")
    private int cronDelay;

    public OrderDispatcher(DinePizzaService dinePizzaService) {
        this.dinePizzaService = dinePizzaService;
        this.cronDelay = cronDelay * 1000;
    }

    /**
     * Cron that will check for ovens availability
     */
    @Scheduled(fixedRateString = "${order-cron-delay}")
    public void execute() {
        if (dinePizzaService.hasFreeOvens() && dinePizzaService.hasPendingOrders())
            dinePizzaService.processOrders();
    }

    public long getDelay() {
        return cronDelay;
    }
}
