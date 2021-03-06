package server.dine.pizza;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class DinePizzasServerApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DinePizzasServerApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
