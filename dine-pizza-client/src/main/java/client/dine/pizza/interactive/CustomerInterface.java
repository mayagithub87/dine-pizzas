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
    private Scanner scanner;
    List<Topping> toppinsSelected;
    List<Topping> toppinsAllSelected;
    List<Topping> toppinsAvailables;

    public CustomerInterface(OrderClient orderClient) {
        this.orderClient = orderClient;
        scanner = new Scanner(System.in);
        toppinsSelected = new ArrayList<Topping>();
        toppinsAllSelected = new ArrayList<Topping>();
        toppinsAvailables = new ArrayList<Topping>();
    }

    public void printWelcomeMessage() {
        try {
            // menu
            System.out.println("\n ****************************************\n");
            System.out.println("\n *** WELCOME TO DINE-IN PIZZA SERVICE ***\n");
            System.out.println("\n ****************************************\n");

            System.out.println("\n****************************************");
            System.out.println("************** MAIN MENU ***************");
            System.out.println("****************************************\n");
            System.out.println(" - Option #1 Create Order");
            System.out.println(" - Option #2 View Order Status");
            System.out.println("\n Please type now the option number:");
            // main menu options
            boolean validOption = false;
            do {
                switch(scanner.nextLine()) {
                    // create order
                    case "1":
                        validOption = true;
                        createOrder();
                        break;
                    // view order status
                    case "2":
                        validOption = true;
                        break;
                    default:
                        System.out.println("\n Invalid Option. Please try again.");
                }                
            } while (!validOption);
            
        } catch (Exception e) {
            System.out.println("\n SYSTEM ERROR:");
            System.out.println(e.getStackTrace());
        }
    }

    private void createOrder() {
        try {
            System.out.println("\n******************************************");
            System.out.println("****** CREATE A PIZZA ORDER MENU *********");
            System.out.println("******************************************\n");
            // ask for customer name
            System.out.println(" Please type your name for the order:");
            String name = scanner.nextLine();
            Order newOrder = new Order(name);
            toppinsAllSelected = new ArrayList<Topping>();
            // menu for adding pizza to the new order
            boolean addPizza = true;
            do {
                System.out.println("\n******************************************");
                System.out.println("******** ADD PIZZA TO ORDER MENU *********");
                System.out.println("******************************************\n");
                // display existing pizzas to the order
                if (newOrder.getPizzas().size() > 0) {
                    System.out.println(" Selected Pizza(s):\n");
                    for (int index = 0; index < newOrder.getPizzas().size(); index++) {
                        System.out.println(String.format(" - #%d: %s \n", index+1, newOrder.getPizzas().get(index).toString()));
                    }
                    System.out.println(" Menu options:\n");
                }
                // display order menu
                System.out.println(" - Option #1 Add Pizza");
                System.out.println(" - Option #2 Finish and Send Order");
                System.out.println(String.format("\n %s, please type the option number\n Any invalid option will take you to the Main Menu", name));
                // order menu options
                switch(scanner.nextLine()) {
                    // call method to add toppings to current pizza
                    case "1":
                        addPizzaToppings();
                        newOrder.getPizzas().add( new Pizza(toppinsSelected) );
                        break;
                    // finish the order and send it to the kitchen (serve)
                    case "2":
                        addPizza = false;
                        //finish order and send it
                        sendOrder(newOrder);
                        break;
                    // any invalid option will return to main menu
                    default:
                        addPizza = false;
                        break;
                }     
            } while (addPizza);

        } catch (Exception e) {
            System.out.println("\n SYSTEM ERROR:");
            System.out.println(e.getStackTrace());
        } finally {
            // return to main menu
            printWelcomeMessage();
        }
    }

    private void addPizzaToppings() {
        // reset toppings selected and availables lists
        toppinsSelected = new ArrayList<Topping>();
        toppinsAvailables = new ArrayList<Topping>();
        try {
            // get toppings for server
            toppinsAvailables = orderClient.getToppins();
            if (toppinsAvailables != null) {
                // remove unavailable toppings and already on previous pizza selected
                for(Topping available: toppinsAvailables) {
                    for(Topping selected: toppinsAllSelected) {
                        if (available.getName().equals(selected.getName()) ) {
                            available.setQuantity(available.getQuantity() - selected.getQuantity());
                            break;
                        }
                    }
                    if (available.getQuantity() < 1) {
                        toppinsAvailables.remove(available);
                    }
                }
            }
            // check if exists available toppings
            if (toppinsAvailables == null || toppinsAvailables.isEmpty()) {
                System.out.println("\n Sorry, there are not available toppings to add the pizza.");
                System.out.println("\n Press any key to return to the Previous Menu.");
                scanner.nextLine();
            } else {
                // menu for adding toppings to the new pizza
                boolean addTopping = true;
                do {
                    System.out.println("\n**********************************************");
                    System.out.println("***** SELECT TOPPINGS FOR CURRENT PIZZA ******");
                    System.out.println("**********************************************\n");
                    // display selected toppings for current pizza
                    if(toppinsSelected.size()>0) {
                        System.out.println(" Selected Topping(s):\n");
                        toppinsSelected.forEach(topping -> System.out.println(" - " + topping.toString()));
                        System.out.println("\n Available Topping(s):\n");
                    }
                    // display available toppings for current pizza
                    for (int index = 0; index < toppinsAvailables.size(); index++) {
                        System.out.println(String.format(" - Option #%d: %s", index+1, toppinsAvailables.get(index).toString()));
                    }
                    System.out.println("\n Please type the option number\n Any invalid option will take you to the previous Menu");
                    // menu option for adding toppings
                    int toppingPosition = 0;
                    int toppingQuantity = 0;
                    String toppingPos = scanner.nextLine();
                    try {
                        toppingPosition = Integer.parseInt(toppingPos);
                    }
                    catch (NumberFormatException e){
                        addTopping = false;
                        break;
                    }
                    // ask for topping quantity
                    if (toppingPosition > 0 && toppingPosition <= toppinsAvailables.size()) {
                        Topping toppingSelected = toppinsAvailables.get(toppingPosition-1); 
                        System.out.println(String.format("\n Please type now the quantity of %s you would like to add between 1 and %d \n If you type any other value the system will select 1 for you", toppingSelected.getName(), toppingSelected.getQuantity()));
                        String toppingQty = scanner.nextLine();
                        try {
                            toppingQuantity = Integer.parseInt(toppingQty);
                            if(toppingQuantity > toppingSelected.getQuantity() || toppingQuantity < 1) {
                                toppingQuantity = 1;
                            }
                        }
                        catch(NumberFormatException e){
                            toppingQuantity = 1;
                        }
                        toppinsSelected.add(new Topping(toppingSelected.getName(), toppingQuantity));
                        toppingSelected.setQuantity(toppingSelected.getQuantity() - toppingQuantity);
                        boolean alreadyToppingSelected = false;
                        for(Topping allSelected: toppinsAllSelected) {
                            if (allSelected.getName() == toppingSelected.getName()) {
                                alreadyToppingSelected = true;
                                allSelected.setQuantity(allSelected.getQuantity() - toppingQuantity);
                            }
                        }
                        if (!alreadyToppingSelected) {
                            toppinsAllSelected.add(new Topping(toppingSelected.getName(), toppingQuantity));
                        }
                    } else {
                        addTopping = false;
                    }
                } while (addTopping);
            }
            
        } catch (Exception e) {
            System.out.println("\n SYSTEM ERROR:");
            System.out.println(e.getStackTrace());
        } 
    }

    private void sendOrder(Order newOrder) {
        try {
            System.out.println("\n Sending order ...");
            Order addedOrder = orderClient.addOrder(newOrder);
            System.out.println("\n Order sent successfully:\n");
            System.out.println(" - " + addedOrder.toString());
        } catch (Exception e) {
            System.out.println("\n SYSTEM ERROR:");
            System.out.println(e.getStackTrace());
        } finally {
            System.out.println("\n Type any value to return to the previous Menu");
            scanner.nextLine();
        }
    }

}
