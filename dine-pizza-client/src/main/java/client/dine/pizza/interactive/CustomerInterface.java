package client.dine.pizza.interactive;

import client.dine.pizza.consumer.DinePizzaApi;
import client.dine.pizza.domain.Order;
import client.dine.pizza.domain.Pizza;
import client.dine.pizza.domain.Topping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entry point between customer (through command line) and server api.
 */
@Component
public class CustomerInterface {

    private DinePizzaApi api;
    private Scanner scanner;

    /**
     * Constructor of the interface - Init.
     */
    public CustomerInterface(DinePizzaApi api) {
        this.api = api;
        scanner = new Scanner(System.in);
    }

    /**
     * Main menu
     * Main menu showing list of all options for the client.
     * 
     * @return
     */
    public void printWelcomeMessage() {
        try {
            // main menu options
            boolean exitMenu = false;
            do {
                // menu display
                System.out.println("\n ****************************************\n");
                System.out.println("\n *** WELCOME TO DINE-IN PIZZA SERVICE ***\n");
                System.out.println("\n ****************************************\n");

                System.out.println("\n****************************************");
                System.out.println("************** MAIN MENU ***************");
                System.out.println("****************************************\n");
                System.out.println(" - Option #1 Create Order");
                System.out.println(" - Option #2 View Orders");
                System.out.println(" - Option #3 Exit");
                System.out.printf("\n Please select option: ");
                // switch with options
                switch(scanner.nextLine()) {
                    // create order
                    case "1":
                        createOrder();
                        break;
                    // view order status
                    case "2":
                        getOrders();
                        break;
                    // exit
                    case "3":
                        exitMenu = true;
                        System.out.println("\n Good bye. Come back soon! \n\n");
                        break;
                    default:
                        System.out.printf("\n Invalid Option. Please try again: ");
                }                
            } while (!exitMenu);
            
        } catch (Exception e) {
            System.out.printf("\n SYSTEM ERROR: %s", e.getMessage());
        }
    }

    /**
     * Main menu > Option #1 Create Order
     * Show submenu with options to create a new order.
     * 
     * @return
     */
    private void createOrder() {
        try {
            System.out.println("\n******************************************");
            System.out.println("****** CREATE A PIZZA ORDER MENU *********");
            System.out.println("******************************************\n");
            // ask for customer name
            System.out.printf(" Please type your name for the order: ");
            String name = scanner.nextLine();
            Order newOrder = new Order(name);
            List<Topping> toppinsSelected = new ArrayList<Topping>();
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
                        System.out.printf(" - #%d: %s \n", index+1, newOrder.getPizzas().get(index).toString());
                    }
                    System.out.println("\n Menu options:\n");
                }
                // display order menu
                System.out.println(" - Option #1 Add Pizza");
                System.out.println(" - Option #2 Finish and Send Order");
                System.out.println(" - Option #3 Return to Main Menu");
                System.out.printf("\n %s, please select option: ", name);
                // order menu options
                switch(scanner.nextLine()) {
                    // call method to add toppings to current pizza
                    case "1":
                        List<Topping> pizzaToppings = addPizzaToppings(toppinsSelected);
                        newOrder.getPizzas().add( new Pizza(pizzaToppings) );
                        pizzaToppings.stream().forEach(topping -> {
                            String currentName = topping.getName();
                            if (toppinsSelected.stream().anyMatch(t -> t.getName().equalsIgnoreCase(currentName))) {
                                toppinsSelected.stream()
                                        .filter(t -> t.getName().equalsIgnoreCase(currentName))
                                        .forEach(tu -> tu.setQuantity(tu.getQuantity() + topping.getQuantity()));
                            } else {
                                toppinsSelected.add(new Topping(currentName, topping.getQuantity()));
                            }
                        });
                        break;
                    // finish the order and send it to the kitchen (serve)
                    case "2":
                        addPizza = false;
                        //finish order and send it
                        sendOrder(newOrder);
                        break;
                    // return to main menu
                    case "3":
                        addPizza = false;
                        break;
                    default:
                        System.out.printf("\n Invalid Option. Please try again: ");
                }     
            } while (addPizza);

        } catch (Exception e) {
            System.out.printf("\n SYSTEM ERROR: %s", e.getMessage());
        } 
    }

    /**
     * Main menu > Option #1 Create Order > Option #1 Add Pizza
     * Show submenu level 2 with options to add toppings to current pizza.
     * 
     * @return
     */
    private List<Topping> addPizzaToppings(List<Topping> toppingsSelected) {
        List<Topping> selection = new ArrayList<Topping>();
        try {
            List<Topping> availables = api.getToppins();
            if (availables != null) {
                // remove unavailable toppings and already on previous pizza selected
                toppingsSelected.stream().forEach(topping -> {
                    String currentName = topping.getName();
                    if (availables.stream().anyMatch(t -> t.getName().equalsIgnoreCase(currentName))) {
                        availables.stream()
                                .filter(t -> t.getName().equalsIgnoreCase(currentName))
                                .forEach(tu -> {
                                    tu.setQuantity(tu.getQuantity() - topping.getQuantity());
                                    if (tu.getQuantity() < 1) {
                                        availables.remove(tu);
                                    }
                                });
                    } 
                });
            }
            // check if exists available toppings
            if (availables == null || availables.isEmpty()) {
                System.out.println("\n Sorry, there are not available toppings to add the pizza.");
                System.out.printf("\n Press any key to return to the Previous Menu: ");
                scanner.nextLine();
            } else {
                // menu for adding toppings to the new pizza
                boolean addTopping = true;
                do {
                    System.out.println("\n**********************************************");
                    System.out.println("***** SELECT TOPPINGS FOR CURRENT PIZZA ******");
                    System.out.println("**********************************************\n");
                    // display selected toppings for current pizza
                    if(selection.size()>0) {
                        System.out.println(" Selected Topping(s):\n");
                        selection.forEach(topping -> System.out.printf(" - %s", topping.toString()));
                        System.out.println("\n\n Available Topping(s):\n");
                    }
                    // display available toppings for current pizza
                    for (int index = 0; index < availables.size(); index++) {
                        System.out.printf(" - Option #%d: %s \n", index+1, availables.get(index).toString());
                    }
                    System.out.printf(" - Option #%d: Save and Return to Order Menu", availables.size()+1);
                    System.out.printf("\n\n Please type the option number: ");
                    try {
                        // menu option for adding toppings
                        int toppingPosition = 0;
                        int toppingQuantity = 0;
                        String toppingPos = scanner.nextLine();
                        toppingPosition = Integer.parseInt(toppingPos);
                        // option to return to previous menu
                        if (toppingPosition == availables.size() + 1) {
                            // if there are no toppings add confirmation
                            if (selection.size() < 1) {
                                boolean invalidOptionConfirm = true;
                                do {
                                    System.out.printf("\n You have not selected any toppings for current pizza!");
                                    System.out.printf("\n Do you want that pizza with NO toppings? Y/N: ");
                                    String confirmNoTopping = scanner.nextLine(); 
                                    if (confirmNoTopping.equalsIgnoreCase("Y")) {
                                        invalidOptionConfirm = false;
                                        addTopping = false;
                                    } else if (confirmNoTopping.equalsIgnoreCase("N")) {
                                        invalidOptionConfirm = false;
                                    }
                                } while (invalidOptionConfirm);                                
                            } else {
                                addTopping = false;
                            }
                        } else if (toppingPosition > 0 && toppingPosition <= availables.size()) {
                            // ask for topping quantity
                            Topping toppingSelected = availables.get(toppingPosition-1); 
                            boolean selectQuantity = true;
                            do {
                                System.out.printf("\n Please select %s quantity between 1 and %d: ", toppingSelected.getName(), toppingSelected.getQuantity());
                                String toppingQty = scanner.nextLine();
                                try {
                                    toppingQuantity = Integer.parseInt(toppingQty);
                                    if(toppingQuantity > 0 && toppingQuantity <= toppingSelected.getQuantity()) {
                                        selectQuantity = false;
                                    } 
                                }
                                catch(NumberFormatException e){
                                    // no action, the sub menu will restart
                                } finally {
                                    if (selectQuantity) {
                                        System.out.printf("\n Invalid Quantity. Please try again\n");
                                    } 
                                }
                            } while (selectQuantity);
                            final int qtySelected = toppingQuantity;
                            // update available list of toppings
                            availables.stream().filter(topping -> topping.getName().equalsIgnoreCase(toppingSelected.getName())).forEach(topping -> {
                                topping.setQuantity(topping.getQuantity() - qtySelected);
                            });
                            // update selection list of toppings
                            AtomicBoolean exist = new AtomicBoolean(false);
                            selection.stream().filter(topping -> topping.getName().equalsIgnoreCase(toppingSelected.getName())).forEach(topping -> {
                                exist.set(true);
                                topping.setQuantity(topping.getQuantity() + qtySelected);
                            });
                            if (!exist.get()) {
                                selection.add(new Topping(toppingSelected.getName(), qtySelected));
                            }
                        } else {
                            System.out.printf("\n Invalid Option. Please try again\n");
                        }
                    }
                    catch (NumberFormatException e){
                        System.out.printf("\n Invalid Option. Please try again\n");
                    }
                } while (addTopping);
            }
        } catch (Exception e) {
            System.out.printf("\n SYSTEM ERROR: %s", e.getMessage());
        } finally {
            return selection;
        }
    }

    /**
     * Main menu > Option #1 Create Order > Option #2 Finish and Send Order
     * Show submenu level 2 with options to finish and send the order.
     * 
     * @return
     */
    private void sendOrder(Order newOrder) {
        try {
            System.out.println("\n Sending order ...");
            Order addedOrder = api.addOrder(newOrder);
            System.out.println("\n Order sent successfully:\n");
            System.out.printf(" - %s", addedOrder.toString());
        } catch (Exception e) {
            System.out.printf("\n SYSTEM ERROR: %s", e.getMessage());
        } finally {
            System.out.println("\n Type any value to return to the previous Menu");
            scanner.nextLine();
        }
    }

    /**
     * Main menu > Option #2 View Orders
     * Show submenu with options to display all order status.
     * 
     * @return
     */
    private void getOrders() {
        try {
            System.out.println("\n**********************************************");
            System.out.println("**************** VIEW ORDERS *****************");
            System.out.println("**********************************************\n");
            List<Order> orders = api.getOrders();
            if (orders == null || orders.isEmpty()) {
                System.out.println("\n Sorry, there are not available orders on the server.\n Please, create a new one.");
            } else {
                // display all order formatted
                for (int index = 0; index < orders.size(); index++) {
                    System.out.printf(" - #%d: %s \n", index+1, orders.get(index).toString());
                }
            }
        } catch (Exception e) {
            System.out.printf("\n SYSTEM ERROR: %s", e.getMessage());
        } finally {
            System.out.printf("\n\n Type any value to return to the Main Menu: ");
            scanner.nextLine();
        }
    }

}
