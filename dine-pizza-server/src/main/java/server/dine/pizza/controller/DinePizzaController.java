package server.dine.pizza.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.dine.pizza.domain.model.Order;
import server.dine.pizza.domain.model.Oven;
import server.dine.pizza.domain.model.Topping;
import server.dine.pizza.service.DinePizzaService;

import javax.validation.Valid;
import java.util.List;

@Api("Dine pizza information is manage here")
@RestController
public class DinePizzaController {

    private DinePizzaService dinePizzaService;

    public DinePizzaController(DinePizzaService dinePizzaService) {
        this.dinePizzaService = dinePizzaService;
    }

    @ApiOperation(value = "Updates ovens baking time")
    @PutMapping(value = "/ovens")
    public void updateBakingTime(@Valid @RequestParam("baking-time") int bakingTime) {
        dinePizzaService.updateBakingTime(bakingTime);
    }

    @ApiOperation(value = "Adds a new oven", response = Order.class)
    @PostMapping(value = "/ovens")
    public Oven addOven() {
        return dinePizzaService.addOven();
    }
//    public Oven addOven(@Valid @RequestBody Oven oven) {
//        return dinePizzaService.addOven(oven);
//    }

    @ApiOperation(value = "Returns list of available toppings for making orders")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/toppings")
    public List<Topping> availableToppings() {
        return dinePizzaService.retrieveToppings();
    }

}
