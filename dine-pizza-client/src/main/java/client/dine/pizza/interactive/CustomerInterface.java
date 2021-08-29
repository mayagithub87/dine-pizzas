package client.dine.pizza.interactive;

import client.dine.pizza.consumer.OrderClient;
import client.dine.pizza.domain.Order;
import client.dine.pizza.domain.Pizza;
import client.dine.pizza.domain.Topping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point between customer (through command line) and server api.
 */
@Component
public class CustomerInterface {

    private OrderClient orderClient;

    public CustomerInterface(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public void printWelcomeMessage() {
        try {
            // menu
            System.out.println("\n \n \n Welcome to Dine Pizza Service! \n\n Main Menu:\n");
            System.out.println(" - Option #1 Create Order");
            System.out.println(" - Option #2 View Order Status");
            System.out.println("\n Please type now the option number:");
            // options
            Scanner scanner = new Scanner(System.in);
            boolean validOption = false;
            do {
                switch(scanner.next()) {
                    case "1":
                        validOption = true;
                        //scanner.close();
                        createOrder();
                        break;
                    case "2":
                        validOption = true;
                        //scanner.close();
                        break;
                    default:
                        System.out.println("\n Invalid Option. Please try again.");
                }                
            } while (!validOption);
            // scanner.close();
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void addOrder() {
        //TODO retreive input and create order
        Order addedOrder = orderClient.addOrder(new Order());

    }

    public void createOrder() {
        try {
            List<Topping> toppins = orderClient.getToppins();
            System.out.println("\n Please type your name for the order:\n");
            
            Scanner scanner1 = new Scanner(System.in);
            String name = scanner1.next();
            Order newOrder = new Order(name);
            boolean addPizza = true;
            do {
                // menu
                if (newOrder.getPizzas().size() > 0) {
                    System.out.println("\n Selected Pizza(s):\n");
                    for (int index = 0; index < newOrder.getPizzas().size(); index++) {
                        Pizza p = newOrder.getPizzas().get(index);
                        System.out.println(String.format(" - #%d: %s ", index+1, p.toString()));
                    }
                }
                System.out.println(String.format("\n Please %s select any of the following options", name));
                System.out.println(" - Option #1 Add Pizza");
                System.out.println(" - Option #2 Finish Order");
                System.out.println("\n Any other key will return to the Main Menu.\n Please type now the option number:");
                // options
                boolean validOption = false;
                Scanner scanner2 = new Scanner(System.in);
                switch(scanner2.next()) {
                    case "1":
                        List<Topping> toppinsSelected = addToppings(toppins);
                        newOrder.getPizzas().add( new Pizza(toppinsSelected) );
                        break;
                    case "2":
                        addPizza = false;
                        //finish order and send it
                        Order addedOrder = orderClient.addOrder(newOrder);
                        break;
                    default:
                        printWelcomeMessage();
                }     
            } while (addPizza);

        } catch (Exception e) {
            System.out.println(e.toString());
        } 
    }

    public List<Topping> addToppings(List<Topping> toppins) {
        List<Topping> selected = new ArrayList<Topping>();
        try {
            boolean addTopping = true;
            do {
                if(selected.size()>0) {
                    System.out.println("\n Selected Topping(s):\n");
                    for (int index = 0; index < selected.size(); index++) {
                        Topping t = selected.get(index);
                        System.out.println(String.format(" - #%d: %s (quantity: %d)", index+1, t.getName(), t.getQuantity()));
                    }
                }
                System.out.println("\n Available Topping(s):\n");
                for (int index = 0; index < toppins.size(); index++) {
                    Topping t = toppins.get(index);
                    System.out.println(String.format(" - Option #%d: %s (available: %d)", index+1, t.getName(), t.getQuantity()));
                }
                System.out.println("\n Any other key will return to the Previous Menu.\n Please type now the option number of the topping you would like to add:");
                Scanner scanner3 = new Scanner(System.in);
                if(scanner3.hasNextInt()) {
                    int toppingPosition = scanner3.nextInt();
                    if(toppingPosition > toppins.size() || toppingPosition < 1) {
                        addTopping = false;
                    } else {
                        Topping t = toppins.get(toppingPosition-1); 
                        System.out.println(String.format("\n Please type now the quantity of %s you would like to add between 1 and %d \n If you type any other value the system will select 1 for you", t.getName(), t.getQuantity()));
                        Scanner scanner4 = new Scanner(System.in);
                        int toppingQty = scanner4.nextInt();
                        if(toppingQty > t.getQuantity() || toppingQty < 1) {
                            toppingQty = 1;
                        }
                        selected.add(new Topping(t.getName(), toppingQty));
                        t.setQuantity(t.getQuantity() - toppingQty);
                    }
                }
            } while (addTopping);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            return selected;
        }
    }

}
