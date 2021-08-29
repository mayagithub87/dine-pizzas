package client.dine.pizza;

import client.dine.pizza.interactive.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DinePizzasClientApp implements CommandLineRunner {

    @Autowired
    private CustomerInterface customerInterface;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DinePizzasClientApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        customerInterface.printWelcomeMessage();
    }
}
