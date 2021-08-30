package server.dine.pizza.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import server.dine.pizza.schedule.OrderDispatcher;
import server.dine.pizza.schedule.OvensManager;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private OrderDispatcher orderDispatcher;

    @Autowired
    private OvensManager ovensManager;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registerTask) {
        registerTask.setScheduler(taskExecutor());
        registerTask.addTriggerTask(
                orderDispatcher::execute,
                context -> {
                    Optional<Date> lastCompletionTime =
                            Optional.ofNullable(context.lastCompletionTime());
                    Instant nextExecutionTime =
                            lastCompletionTime.orElseGet(Date::new).toInstant()
                                    .plusMillis(orderDispatcher.getDelay());
                    return Date.from(nextExecutionTime);
                }
        );
        registerTask.addTriggerTask(
                ovensManager::execute,
                context -> {
                    Optional<Date> lastCompletionTime =
                            Optional.ofNullable(context.lastCompletionTime());
                    Instant nextExecutionTime =
                            lastCompletionTime.orElseGet(Date::new).toInstant()
                                    .plusMillis(ovensManager.getDelay());
                    return Date.from(nextExecutionTime);
                }
        );
    }

}
