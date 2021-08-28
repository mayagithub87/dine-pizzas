package server.dine.pizza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import server.dine.pizza.service.DinePizzaService;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class DinePizzasServerApp implements CommandLineRunner {

    public static final String INVENTORY_ARG = "--inventory=";

    @Autowired
    private DinePizzaService dinePizzaService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DinePizzasServerApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            if (arg.startsWith(INVENTORY_ARG)) {
                String filePath = arg.replaceFirst(INVENTORY_ARG, "");
                dinePizzaService.loadInventory(filePath);
            } else {
                throw new Exception("Inventory file not found.");
            }
        }
    }
}
