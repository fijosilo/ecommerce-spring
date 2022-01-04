package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import com.fijosilo.ecommerce.product.Product;
import com.fijosilo.ecommerce.product.ProductBrand;
import com.fijosilo.ecommerce.product.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
public class OrderController {
    // get orders
    // post order
    // put order
    // delete order
    private final OrderService orderService;
    private final ClientService clientService;
    private final ProductService productService;

    public OrderController(OrderService orderService, ClientService clientService, ProductService productService) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.productService = productService;
    }

    @GetMapping("/order")
    public HashMap<String, Object> readOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
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
    public HashMap<String, Object> readOrders(@RequestParam HashMap<String, String> params, Authentication authentication) {
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

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // validate charge address
        if (client.getChargeAddress() == null) {
            response.put("message", "Client needs to have a charge address defined.");
            return response;
        }

        // validate deliver address
        if (client.getDeliverAddress() == null) {
            response.put("message", "Client needs to have a deliver address defined.");
            return response;
        }

        // validate list of products
        LinkedList<Product> products = new LinkedList<>();
        int i = 0;
        String key = String.format("product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field product_codes can't be blank.", i));
                return response;
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                response.put("message", String.format("Field product_codes[%d] must be a valid product code.", i));
                return response;
            }
            products.add(product);
        }
        if (products.size() == 0) {
            response.put("message", String.format("Field product_codes is required.", i));
            return response;
        }

        // validate payment method
        if (!params.containsKey("payment_method")) {
            response.put("message", "Field payment_method is required.");
            return response;
        }
        String paymentMethod = params.get("payment_method").toUpperCase();
        if (paymentMethod.isBlank()) {
            response.put("message", "Field payment_method can't be blank.");
            return response;
        }
        boolean isPaymentMethodValid = false;
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (paymentMethod.equals(pm.toString())) {
                isPaymentMethodValid = true;
                break;
            }
        }
        if (!isPaymentMethodValid) {
            response.put("message", "Field payment_method must be a valid payment method.");
            return response;
        }

        // all validation tests passed

        // register the order
        Order order = new Order();
        order.setCode(UUID.randomUUID().toString());
        order.setClient(client);
        for (Product p : products) {
            order.addProduct(p);
        }
        order.setDate(new Timestamp(System.currentTimeMillis()));
        order.setChargeAddress(client.getChargeAddress().toString());
        order.setPaymentMethod(paymentMethod);
        order.setPaid(false);
        order.setDeliverAddress(client.getDeliverAddress().toString());
        order.setStatus("Waiting for payment");
        order.setFulfilled(false);
        orderService.createOrder(order);

        // TODO: respond with payment details
        response.put("error", false);
        response.put("order", order);
        return response;
    }

    @PutMapping("/admin/order")
    public HashMap<String, Object> updateOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate order code
        if (!params.containsKey("code")) {
            response.put("message", "Field code is required.");
            return response;
        }
        String code = params.get("code");
        if (code.isBlank()) {
            response.put("message", "Field code can't be blank.");
            return response;
        }
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            response.put("message", "Field code must be a valid order code.");
            return response;
        }

        // validate product codes add list
        LinkedList<Product> addProducts = new LinkedList<>();
        int i = 0;
        String key = String.format("add_product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field add_product_codes[%d] can't be blank.", i));
                return response;
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                response.put("message", String.format("Field add_product_codes[%d] must be a valid product code.", i));
                return response;
            }
            addProducts.add(product);
        }

        // validate product codes remove list
        LinkedList<Product> remProducts = new LinkedList<>();
        i = 0;
        key = String.format("rem_product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field rem_product_codes[%d] can't be blank.", i));
                return response;
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                response.put("message", String.format("Field rem_product_codes[%d] must be a valid product code.", i));
                return response;
            }
            remProducts.add(product);
        }

        // validate charge address
        String chargeAddress = null;
        if (params.containsKey("charge_address")) {
            chargeAddress = params.get("charge_address");
            if (chargeAddress.isBlank()) {
                response.put("message", "Field charge_address can't be blank.");
                return response;
            }
        }

        // validate deliver address
        String deliverAddress = null;
        if (!params.containsKey("deliver_address")) {
            deliverAddress = params.get("deliver_address");
            if (deliverAddress.isBlank()) {
                response.put("message", "Field deliver_address can't be blank.");
                return response;
            }
        }

        // validate paid
        Boolean isPaid = null;
        if (!params.containsKey("order_paid")) {
            String isPaidString = params.get("order_paid").toLowerCase();
            if (isPaidString.isBlank()) {
                response.put("message", "Field order_paid can't be blank.");
                return response;
            }
            if (!isPaidString.equals("true") && !isPaidString.equals("false")) {
                response.put("message", "Field order_paid must be true or false.");
                return response;
            }
            isPaid = Boolean.parseBoolean(isPaidString);
        }

        // status
        String status = null;
        if (!params.containsKey("status")) {
            status = params.get("status");
            if (status.isBlank()) {
                response.put("message", "Field status can't be blank.");
                return response;
            }
        }

        // all validations test passed

        // update order
        // TODO: add products and remove products from order needs a quantity param to be passed before continuing with this


        response.put("error", false);
        return response;
    }

    @DeleteMapping("/order")
    public HashMap<String, Object> deleteOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // validate order code
        if (!params.containsKey("code")) {
            response.put("message", "Field code is required.");
            return response;
        }
        String code = params.get("code");
        if (code.isBlank()) {
            response.put("message", "Field code can't be blank.");
            return response;
        }
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            response.put("message", "Field code must be a valid order code.");
            return response;
        }
        // TODO: need to validate the this order belongs to this client
        // if the order was already been paid inform the client the support needs to be contacted to cancel the order
        if (order.isPaid()) {
            response.put("message", "Order has already been paid. Contact the support to cancel your order");
            return response;
        }

        // all validations test passed

        // TODO: cancel the payment request

        // delete order
        order.setStatus("Cancelled by the client");
        orderService.deleteOrder(order);

        response.put("error", false);
        return response;
    }

    @DeleteMapping("/admin/order")
    public HashMap<String, Object> adminDeleteOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate order code
        if (!params.containsKey("code")) {
            response.put("message", "Field code is required.");
            return response;
        }
        String code = params.get("code");
        if (code.isBlank()) {
            response.put("message", "Field code can't be blank.");
            return response;
        }
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            response.put("message", "Field code must be a valid order code.");
            return response;
        }

        // all validations test passed

        // TODO: cancel the payment request
        if (!order.isPaid()) {
            //
        }

        // delete order
        order.setStatus("Cancelled");
        orderService.deleteOrder(order);

        response.put("error", false);
        return response;
    }

}
