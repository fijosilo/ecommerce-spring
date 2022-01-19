package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import com.fijosilo.ecommerce.product.Product;
import com.fijosilo.ecommerce.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final ClientService clientService;
    private final ProductService productService;

    public OrderController(OrderService orderService, ClientService clientService, ProductService productService) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.productService = productService;
    }

    @GetMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }

        // validate order code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate order
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate that the requested order belongs to the client
        if (order.getClient() != client) {
            // if it doesn't, behave has if the order doesn't even exist
            // because we don't want clients to know anything about other clients orders
            payload.put("error", "An order with the code provided was not found.");
            return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
        }

        // all validations test passed

        payload.put("order", order);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/admin/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> adminReadOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate order code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate order
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        payload.put("order", order);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readOrders(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            payload.put("error", "This endpoint must be accessed while authenticated.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate maximum orders per page
        Integer maxOrdersPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                payload.put("error", "Field maximum products per page can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxOrdersPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field maximum products per page must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxOrdersPerPage < 1) {
                payload.put("error", "Field maximum products per page can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                payload.put("error", "Field page number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field page number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (pageNumber < 1) {
                payload.put("error", "Field page number can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // get orders list
        List<Order> orders = orderService.readOrdersByClient(client, maxOrdersPerPage, pageNumber);

        payload.put("orders", orders);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/admin/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> adminReadOrders(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // optional validate client
        Client client = null;
        if (params.containsKey("client_email")) {
            String email = params.get("client_email");
            if (email.isBlank()) {
                payload.put("error", "Field client_email can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            client = clientService.readClientByEmail(email);
            if (client == null) {
                payload.put("error", "Field client_email must be a valid client email.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate order minimum date
        Long minDate = null;
        if (params.containsKey("min_order_date")) {
            String minDateString = params.get("min_order_date");
            if (minDateString.isBlank()) {
                payload.put("error", "Field min_order_date can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                minDate = Long.parseLong(minDateString);
            } catch (IllegalArgumentException e) {
                payload.put("error", "Field min_order_date must follow the format yyyy-[m]m-[d]d hh:mm:ss[.f...].");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate order maximum date
        Long maxDate = null;
        if (params.containsKey("max_order_date")) {
            String maxDateString = params.get("max_order_date");
            if (maxDateString.isBlank()) {
                payload.put("error", "Field max_order_date can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxDate = Long.parseLong(maxDateString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field max_order_date must follow the format yyyy-[m]m-[d]d hh:mm:ss[.f...].");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate minimum date in relation to maximum date
        if (minDate != null && maxDate == null) {
            maxDate = System.currentTimeMillis();
        }
        if (maxDate != null && minDate == null) {
            minDate = 0L;
        }
        if (minDate != null && maxDate != null) {
            if (minDate > maxDate) {
                payload.put("error", "Field min_order_date can't be older than max_order_date.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate order payment method
        PaymentMethod paymentMethod = null;
        if (params.containsKey("order_payment_method")) {
            String paymentMethodString = params.get("order_payment_method");
            if (paymentMethodString.isBlank()) {
                payload.put("error", "Field order_payment_method can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            for (PaymentMethod pm : PaymentMethod.values()) {
                if (paymentMethodString.equals(pm.toString())) {
                    paymentMethod = pm;
                    break;
                }
            }
            if (paymentMethod == null) {
                payload.put("error", "Field order_payment_method must be a valid payment method.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate order is paid
        Boolean isPaid = null;
        if (params.containsKey("order_is_paid")) {
            String isPaidString = params.get("order_is_paid").toLowerCase();
            if (isPaidString.isBlank()) {
                payload.put("error", "Field order_is_paid can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!isPaidString.equals("true") && !isPaidString.equals("false")) {
                payload.put("error", "Field order_is_paid must be true or false.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            isPaid = Boolean.parseBoolean(isPaidString);
        }

        // optional validate order status
        String status = null;
        if (params.containsKey("order_status")) {
            status = params.get("order_status");
            if (status.isBlank()) {
                payload.put("error", "Field order_status can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate order is fulfilled
        Boolean isFulfilled = null;
        if (params.containsKey("order_is_fulfilled")) {
            String isFulfilledString = params.get("order_is_fulfilled").toLowerCase();
            if (isFulfilledString.isBlank()) {
                payload.put("error", "Field order_is_fulfilled can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!isFulfilledString.equals("true") && !isFulfilledString.equals("false")) {
                payload.put("error", "Field order_is_fulfilled must be true or false.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            isFulfilled = Boolean.parseBoolean(isFulfilledString);
        }

        // optional validate maximum orders per page
        Integer maxOrdersPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                payload.put("error", "Field maximum products per page can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxOrdersPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field maximum products per page must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxOrdersPerPage < 1) {
                payload.put("error", "Field maximum products per page can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                payload.put("error", "Field page number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field page number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (pageNumber < 1) {
                payload.put("error", "Field page number can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // get orders list
        List<Order> orders = orderService.readOrdersByFilters(client, minDate, maxDate, paymentMethod, isPaid,
                isFulfilled, maxOrdersPerPage, pageNumber);

        payload.put("orders", orders);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> createOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            payload.put("error", "This endpoint must be accessed while authenticated.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate charge address
        if (client.getChargeAddress() == null) {
            payload.put("error", "Client needs to have a charge address defined.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate deliver address
        if (client.getDeliverAddress() == null) {
            payload.put("error", "Client needs to have a deliver address defined.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate list of products
        LinkedList<Product> products = new LinkedList<>();
        int i = 0;
        String key = String.format("product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field product_codes can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                payload.put("error", String.format("Field product_codes[%d] must be a valid product code.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            products.add(product);

            i++;
            key = String.format("product_codes[%d]", i);
        }
        if (products.size() == 0) {
            payload.put("error", String.format("Field product_codes is required.", i));
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate payment method
        if (!params.containsKey("payment_method")) {
            payload.put("error", String.format("Field payment_method is required.", i));
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String paymentMethod = params.get("payment_method").toUpperCase();
        if (paymentMethod.isBlank()) {
            payload.put("error", "Field payment_method can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        boolean isPaymentMethodValid = false;
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (paymentMethod.equals(pm.toString())) {
                isPaymentMethodValid = true;
                break;
            }
        }
        if (!isPaymentMethodValid) {
            payload.put("error", "Field payment_method must be a valid payment method.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validation tests passed

        // register the order
        Order order = new Order();
        order.setCode(UUID.randomUUID().toString());
        order.setClient(client);
        for (Product p : products) {
            order.addProduct(p);
        }
        order.setDate(System.currentTimeMillis());
        order.setChargeAddress(client.getChargeAddress().toString());
        order.setPaymentMethod(paymentMethod);
        order.setPaid(false);
        order.setDeliverAddress(client.getDeliverAddress().toString());
        order.setStatus("Waiting for payment");
        order.setFulfilled(false);
        orderService.createOrder(order);

        // TODO: respond with payment details

        payload.put("order", order);
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> adminUpdateOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate order code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate product codes add list
        LinkedList<Product> addProducts = new LinkedList<>();
        int i = 0;
        String key = String.format("add_product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field add_product_codes[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                payload.put("error", String.format("Field add_product_codes[%d] must be a valid product code.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            addProducts.add(product);

            i++;
            key = String.format("add_product_codes[%d]", i);
        }

        // optional validate product codes remove list
        LinkedList<Product> remProducts = new LinkedList<>();
        i = 0;
        key = String.format("rem_product_codes[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field rem_product_codes[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String productCode = params.get(key);
            Product product = productService.readProductByCode(productCode);
            if (product == null) {
                payload.put("error", String.format("Field rem_product_codes[%d] must be a valid product code.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            remProducts.add(product);

            i++;
            key = String.format("rem_product_codes[%d]", i);
        }

        // optional validate charge address
        String chargeAddress = null;
        if (params.containsKey("charge_address")) {
            chargeAddress = params.get("charge_address");
            if (chargeAddress.isBlank()) {
                payload.put("error", "Field charge_address can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate deliver address
        String deliverAddress = null;
        if (params.containsKey("deliver_address")) {
            deliverAddress = params.get("deliver_address");
            if (deliverAddress.isBlank()) {
                payload.put("error", "Field deliver_address can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate paid
        Boolean isPaid = null;
        if (params.containsKey("order_paid")) {
            String isPaidString = params.get("order_paid").toLowerCase();
            if (isPaidString.isBlank()) {
                payload.put("error", "Field order_paid can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!isPaidString.equals("true") && !isPaidString.equals("false")) {
                payload.put("error", "Field order_paid must be true or false.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            isPaid = Boolean.parseBoolean(isPaidString);
        }

        // optional status
        String status = null;
        if (params.containsKey("status")) {
            status = params.get("status");
            if (status.isBlank()) {
                payload.put("error", "Field status can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // TODO: receive map of products and quantities from the front end

        // update order
        for (Product p : addProducts) {
            order.addProduct(p);
        }
        for (Product p : remProducts) {
            order.remProduct(p);
        }
        if (chargeAddress != null) order.setChargeAddress(chargeAddress);
        if (deliverAddress != null) order.setDeliverAddress(deliverAddress);
        if (isPaid != null) order.setPaid(isPaid);
        if (status != null) order.setStatus(status);

        if (!orderService.updateOrder(order)) {
            payload.put("error", "Database couldn't update the order.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @DeleteMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> deleteOrder(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }

        // validate code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate order
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate that the requested order belongs to the client
        if (order.getClient() != client) {
            // if it doesn't, behave has if the order doesn't even exist
            // because we don't want clients to be able to delete other clients orders
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // if the order was already been paid inform the client the support needs to be contacted to cancel the order
        if (order.isPaid()) {
            payload.put("error", "Order has already been paid. Contact the support to cancel your order");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // TODO: cancel the payment request

        // delete order
        order.setStatus("Cancelled by the client");
        orderService.deleteOrder(order);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @DeleteMapping(value = "/admin/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> adminDeleteOrder(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate order
        Order order = orderService.readOrderByCode(code);
        if (order == null) {
            payload.put("error", "Field code must be a valid order code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // TODO: cancel the payment request
        if (!order.isPaid()) {
            //
        }

        // delete order
        order.setStatus("Cancelled");
        orderService.deleteOrder(order);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
