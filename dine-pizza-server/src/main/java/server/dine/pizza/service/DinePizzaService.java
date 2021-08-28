package server.dine.pizza.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.dine.pizza.core.exception.NotValidException;
import server.dine.pizza.domain.tdo.CQueue;
import server.dine.pizza.persistence.model.Order;
import server.dine.pizza.persistence.model.Oven;
import server.dine.pizza.persistence.model.Pizza;
import server.dine.pizza.persistence.model.Topping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class DinePizzaService {

    @Value("${baking-time}")
    private int bakingTime;

    private static final String COMMA_DELIMITER = ",";
    private static final String CSV_HEADER = "TOPPING";

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();

    private Logger logger = LoggerFactory.getLogger(DinePizzaService.class);

    private List<Topping> toppingsInventory = new ArrayList<>();
    private List<Oven> ovens = new ArrayList<>();
    private List<Order> ordersReady = new ArrayList<>();
    private CQueue<Order> ordersPending = new CQueue<>();

    // when you set up the ovens with time and you load the toppings is when you can know that you are ready to work
    // endpoint
    public boolean isReady() {
        return (this.ovens.size() > 0 && this.toppingsInventory.size() > 0);
    }

    public DinePizzaService() {
//        adding two ovens as specified
        //TODO improve this
        ovens.add(new Oven(bakingTime));
        ovens.add(new Oven(bakingTime));
        //the process of backing will be done by a cron
    }

    /**
     * Load inventory of topping when application starts.
     *
     * @param filePath
     */
    public void loadInventory(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(CSV_HEADER))
                    continue;
                String[] values = line.split(COMMA_DELIMITER);
                String name = values[0].toLowerCase().trim();
                int quantity = Integer.parseInt(values[1].trim());
                AtomicBoolean exist = new AtomicBoolean(false);
                toppingsInventory.stream().filter(topping -> topping.getName().equalsIgnoreCase(name)).forEach(topping -> {
                    exist.set(true);
                    topping.setQuantity(topping.getQuantity() + quantity);
                });
                if (!exist.get())
                    toppingsInventory.add(new Topping(name, quantity));
            }
            logger.info(filePath + " loaded.");
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * Has adding order logic.
     *
     * @param order
     * @return
     */
    public Order addOder(Order order) {
        if (checkToppingsAvailability(order.getPizzas()))
            ordersPending.add(order);
        else
            throw new NotValidException(String.format("%s we are sorry, there is no availability for your order.", order.getCustomerName()));

        return order;
    }

    /**
     * Returns true if pizza's order can be served tanking into account current toppings availability.
     * If not returns false.
     *
     * @param pizzas
     * @return
     */
    private boolean checkToppingsAvailability(List<Pizza> pizzas) {

        boolean discounted = false;

        final List<Topping> orderToppings = new ArrayList<>();
        pizzas.stream().forEach(pizza -> {
            pizza.getToppings().stream().forEach(topping -> {
                String currentName = topping.getName();
                if (orderToppings.stream().anyMatch(t -> t.getName().equalsIgnoreCase(currentName))) {
                    orderToppings.stream()
                            .filter(t -> t.getName().equalsIgnoreCase(currentName))
                            .forEach(tu -> tu.setQuantity(tu.getQuantity() + topping.getQuantity()));
                } else {
                    orderToppings.add(new Topping(currentName, topping.getQuantity()));
                }
            });
        });

        writeLock.lock();

        // is there topping availability
        if (!orderToppings.isEmpty() &&
                orderToppings.parallelStream()
                        .noneMatch(topping -> toppingsInventory.parallelStream()
                                .anyMatch(t -> t.getName().equalsIgnoreCase(topping.getName())
                                        && t.getQuantity() < topping.getQuantity()))) {

            // then update inventory
            orderToppings.parallelStream()
                    .forEach(topping -> toppingsInventory.parallelStream()
                            .filter(t -> t.getName().equalsIgnoreCase(topping.getName()))
                            .forEach(tu -> {
                                tu.setQuantity(topping.getQuantity() - tu.getQuantity());
                                if (tu.getQuantity() < 0)
                                    tu.setQuantity(0);
                            }));

            discounted = true;
        }
        writeLock.unlock();

        return discounted;
    }

    /**
     * Updates oven baking time, that will be used when ovens get available.
     *
     * @param bakingTime
     */
    public void updateBakingTime(int bakingTime) {
        this.bakingTime = bakingTime;
    }

    /**
     * Returns available toppings in dine pizza.
     *
     * @return
     */
    public List<Topping> retrieveToppings() {
        return toppingsInventory;
    }
}
