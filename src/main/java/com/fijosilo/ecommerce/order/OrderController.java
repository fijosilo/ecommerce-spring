package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import com.fijosilo.ecommerce.product.Product;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class OrderController {
    // get orders
    // post order
    // put order
    // delete order
    private final OrderService orderService;
    private final ClientService clientService;

    public OrderController(OrderService orderService, ClientService clientService) {
        this.orderService = orderService;
        this.clientService = clientService;
    }

    @GetMapping("/order")
    public HashMap<String, Object> readOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // order code

        // all validations test passed

        response.put("error", false);
        //response.put("orders", orders);
        return response;
    }

    @GetMapping("/orders")
    public HashMap<String, Object> readOrders(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // read client connected ?!
        // page number
        // order per page number

        // all validations test passed

        response.put("error", false);
        //response.put("orders", orders);
        return response;
    }

    @PostMapping("/order")
    public HashMap<String, Object> createOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // TODO add to order:
        //  date
        //  deliver address (keep track of address at time of order)
        //  charge address (keep track of address at time of order)
        //  payment method
        //  paid
        //  products (keep track of price and discount at time of order OrderProduct)
        //  total price

        // validate list of products

        // maybe use the current client addresses and convert them to string and store them together with the order
        // validate deliver address
        // validate charge address

        // validate payment methods
        //

        // all validations test passed

        // register the order
        // set status to wait for payment
        // respond with payment details

        // create the order
        Client client = clientService.readClientByEmail(authentication.getName());

        response.put("error", false);
        response.put("client_email", authentication.getName());
        //response.put("orders", orders);
        return response;
    }

    @PutMapping("/order")
    public HashMap<String, Object> updateOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // order code
        // order status
        // products codes add list
        // products codes remove list

        // all validations test passed

        response.put("error", false);
        //response.put("orders", orders);
        return response;
    }

    @DeleteMapping("/order")
    public HashMap<String, Object> deleteOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // order code

        // all validations test passed

        response.put("error", false);
        //response.put("orders", orders);
        return response;
    }

}
