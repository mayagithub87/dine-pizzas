package server.dine.pizza.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.dine.pizza.model.Order;
import server.dine.pizza.model.Topping;
import server.dine.pizza.service.DinePizzaService;

import javax.validation.Valid;
import java.util.List;

@Api("Orders information is manage here")
@RestController
@RequestMapping(value = "/orders")
public class OrdersController {

    private DinePizzaService dinePizzaService;

    public OrdersController(DinePizzaService dinePizzaService) {
        this.dinePizzaService = dinePizzaService;
    }

    @ApiOperation(value = "Adds a new order", response = Order.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Order addOrder(@Valid @RequestBody Order order) {
        return dinePizzaService.addOder(order);
    }

    @ApiOperation(value = "Updates ovens baking time")
    @PutMapping
    public void updateBakingTime(@Valid @RequestParam("baking-time") int bakingTime) {
        dinePizzaService.updateBakingTime(bakingTime);
    }

    @ApiOperation(value = "Returns list of available toppings for making orders")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Topping> availableToppings() {
        return dinePizzaService.retrieveToppings();
    }

}
