package server.dine.pizza.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.dine.pizza.core.api.exception.NotValidException;
import server.dine.pizza.core.websocket.WebSocketController;
import server.dine.pizza.domain.model.*;
import server.dine.pizza.domain.tdo.CQueue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class DinePizzaService {

    @Autowired
    private WebSocketController webSocketController;

    @Value("${ovens-count}")
    private int ovensCount;

    @Value("${baking-time}")
    public int bakingTime;

    private static final String COMMA_DELIMITER = ",";
    private static final String CSV_HEADER = "ingredient";

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();

    private Logger logger = LoggerFactory.getLogger(DinePizzaService.class);

    private List<Topping> toppingsInventory = new ArrayList<>();
    private List<Oven> ovens = new ArrayList<>();
    private List<Order> readyOrders = new ArrayList<>();
    private CQueue<Order> pendingOrders = new CQueue<>();

    public DinePizzaService() throws IOException {
        retrieveDineData();
        // create ovens
        for (int i = 0; i < ovensCount; i++) {
            ovens.add(new Oven(bakingTime));
        }
    }

    /**
     * Records input user for dine pizza configuration.
     * Inventory.csv
     * Ovens Amount
     * Baking Time
     */
    private void retrieveDineData() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String filePath = null;
        int ovens = 0, bakingTime = 0;

        System.out.println("Dine Pizza Server is about to start");
        System.out.println("Please specify inventory.csv path: ");

        do {
            if ((filePath = scanner.nextLine()) != null && !filePath.isEmpty())
                loadInventory(filePath);
        } while (outOfProvisions());

        System.out.println("Please specify ovens amount: ");
        if ((filePath = scanner.nextLine()) != null && (ovens = Integer.parseInt(filePath)) > 0) {
            this.ovensCount = ovens;
            System.out.printf("Ovens amount set [%d]\n", ovens);
        } else
            System.out.printf("Default ovens amount set [%d]\n", ovens);

        System.out.println("Please ovens baking time in seconds: ");
        if ((filePath = scanner.nextLine()) != null && (bakingTime = Integer.parseInt(filePath)) > 0) {
            this.bakingTime = bakingTime;
            System.out.printf("Baking time set [%d] seconds\n", bakingTime);
        } else
            System.out.printf("Default baking time set [%d] seconds\n", bakingTime);

        System.out.println("Everything is set up!");

    }

    /**
     * Load inventory of topping when application starts.
     *
     * @param filePath
     */
    public void loadInventory(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.trim()))) {
            String line;
            boolean firstline = true;
            while ((line = br.readLine()) != null) {
                if (line.contains(CSV_HEADER) || firstline) {
                    firstline = false;
                    continue;
                }
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
        if (checkToppingsAvailability(order.getPizzas())) {
            order.setStatus(Status.PENDING);
            pendingOrders.add(order);
            webSocketController.sendMessage(String.format("%s your order is pending.", order.getName()));

        } else
            throw new NotValidException(String.format("%s we are sorry, there is no availability for your order.", order.getName()));

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

        //counting order toppings
        final List<Topping> orderToppings = new ArrayList<>();
        pizzas.stream().forEach(pizza -> pizza.getToppings().stream().forEach(topping -> {
            String currentName = topping.getName();
            if (orderToppings.stream().anyMatch(t -> t.getName().equalsIgnoreCase(currentName))) {
                orderToppings.stream()
                        .filter(t -> t.getName().equalsIgnoreCase(currentName))
                        .forEach(tu -> tu.setQuantity(tu.getQuantity() + topping.getQuantity()));
            } else {
                orderToppings.add(new Topping(currentName, topping.getQuantity()));
            }
        }));

        //lock for avoiding topping amount errors
        writeLock.lock();

        // is there topping availability
        if (!orderToppings.isEmpty() &&
                orderToppings.parallelStream()
                        .noneMatch(topping -> toppingsInventory.parallelStream()
                                .anyMatch(topInventory -> topInventory.getName().equalsIgnoreCase(topping.getName())
                                        && topInventory.getQuantity() < topping.getQuantity()))) {

            // then update inventory
            orderToppings.stream()
                    .forEach(topping -> toppingsInventory.stream()
                            .filter(t -> t.getName().equalsIgnoreCase(topping.getName()))
                            .forEach(tu -> {
                                tu.setQuantity(tu.getQuantity() - topping.getQuantity());
                                if (tu.getQuantity() < 0) {
                                    tu.setQuantity(0);
                                    webSocketController.sendMessage(String.format("Dine Pizza run out of %s topping.", tu.getName().toUpperCase()));
                                }
                            }));

            discounted = true;
        }
        // pizzas without toppings may also be baked
        else if (orderToppings.isEmpty())
            discounted = true;

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
        return toppingsInventory.stream().filter(topping -> topping.getQuantity() > 0).collect(Collectors.toList());
    }

    /**
     * Returns true if dine pizza out of toppings provisions.
     *
     * @return
     */
    public boolean outOfProvisions() {
        return toppingsInventory.isEmpty() || toppingsInventory.stream().mapToInt(Topping::getQuantity).sum() == 0;
    }

    /**
     * Returns true if there are pending orders.
     *
     * @return
     */
    public boolean hasPendingOrders() {
        return pendingOrders.size() > 0;
    }

    /**
     * Returns true if there are free ovens for baking orders.
     *
     * @return
     */
    public boolean hasFreeOvens() {
        return ovens.stream().anyMatch(oven -> !oven.isBusy());
    }

    /**
     * Returns true if there are any ovens baking orders already.
     *
     * @return
     */
    public boolean hasBusyOvens() {
        return ovens.stream().anyMatch(oven -> oven.isBusy());
    }

    /**
     * Filters free ovens and bakes pending orders.
     */
    public void processOrder() {
        if (pendingOrders.size() > 0) {

            Order order = pendingOrders.poll();
            order.setStatus(Status.PROCESSING);

            List<Pizza> pizzas = order.getPizzas();
            CQueue<Pizza> pizzasQueue = new CQueue<>();
            pizzasQueue.addAll(pizzas);

            logger.info("processing order {}", order.toString());
            int countdown = order.getPizzas().size() * bakingTime;
            webSocketController.sendMessage(String.format("%s your order is processing.", order.getName()), countdown, order.getName());

            // bake pizzas
            do {

                AtomicInteger ovensOn = new AtomicInteger(0);
                this.ovens.stream().filter(oven -> !oven.isBusy()).limit(pizzasQueue.size()).forEach(oven -> {
                    Pizza pizza = pizzasQueue.poll();
                    if (pizza != null) {
                        ovensOn.getAndIncrement();
                        pizza.setCustomer(order.getName());
                        oven.setOrder(order);
                        oven.bakePizza(pizza);
                    }
                });
//                if (pizzasQueue.size() > 0) {
//sleep
//                }
            } while (pizzasQueue.size() != 0);

            order.setStatus(Status.FINISHED);
            readyOrders.add(order);
            webSocketController.sendMessage(String.format("%s your order is ready.", order.getName()));
        }
    }

    /**
     * Returns all orders and its corresponding statuses.
     *
     * @return
     */
    public List<Order> getOrdersStatus() {
        // baking
        List<Order> list = ovens.stream().filter(Oven::isBusy).map(Oven::getOrder).collect(Collectors.toList());
        if (list == null)
            list = new ArrayList<>();
        else
            //ready
            list.addAll(readyOrders);
        //pending
        if (!pendingOrders.isEmpty())
            list.addAll(pendingOrders);
        return list;
    }

    /**
     * Checks if orders has elapsed baking time for releasing corresponding oven.
     */
    public void releaseOvens() {
        ovens.stream().filter(oven -> oven.isBusy()).forEach(
                oven -> {
                    if (oven.isDone()) {
                        Pizza pizza = oven.getPizza();
                        oven.release();
                        logger.info("oven baked pizza {}", pizza.toString());
                        webSocketController.sendMessage(String.format("%s pizza baked %s.", pizza.getCustomer(), pizza.toString()));
                    }
                }
        );
    }

    /**
     * Adds a new oven to dine pizza.
     *
     * @return
     */
    public Oven addOven() {
        Oven oven = new Oven(bakingTime);
        ovens.add(oven);
        return oven;
    }
}
