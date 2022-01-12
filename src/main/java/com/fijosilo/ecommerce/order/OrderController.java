package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import com.fijosilo.ecommerce.product.Product;
import com.fijosilo.ecommerce.product.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
public class OrderController {
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

        // validate that the requested order belongs to the client
        if (order.getClient() != client) {
            // if it doesn't, behave has if the order doesn't even exist
            // because we don't want clients to know anything about other clients orders
            response.put("message", "Field code must be a valid order code.");
            return response;
        }

        // all validations test passed

        response.put("error", false);
        response.put("order", order);
        return response;
    }

    @GetMapping("/admin/order")
    public HashMap<String, Object> adminReadOrder(@RequestParam HashMap<String, String> params) {
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

        response.put("error", false);
        response.put("order", order);
        return response;
    }

    @GetMapping("/orders")
    public HashMap<String, Object> readOrders(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // optional validate maximum orders per page
        Integer maxOrdersPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                response.put("message", "Field maximum products per page can't be blank.");
                return response;
            }
            try {
                maxOrdersPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                response.put("message", "Field maximum products per page must be a valid integer number.");
                return response;
            }
            if (maxOrdersPerPage < 1) {
                response.put("message", "Field maximum products per page can't be smaller than one.");
                return response;
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                response.put("message", "Field page number can't be blank.");
                return response;
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                response.put("message", "Field page number must be a valid integer number.");
                return response;
            }
            if (pageNumber < 1) {
                response.put("message", "Field page number can't be smaller than one.");
                return response;
            }
        }

        // all validations test passed

        // get orders list
        List<Order> orders = orderService.readOrdersByClient(client, maxOrdersPerPage, pageNumber);

        response.put("error", false);
        response.put("orders", orders);
        return response;
    }

    @GetMapping("/admin/orders")
    public HashMap<String, Object> adminReadOrders(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // optional validate client
        Client client = null;
        if (params.containsKey("client_email")) {
            String email = params.get("client_email");
            if (email.isBlank()) {
                response.put("message", "Field client_email can't be blank.");
                return response;
            }
            client = clientService.readClientByEmail(email);
            if (client == null) {
                response.put("message", "Field client_email must be a valid client email.");
                return response;
            }
        }

        // optional validate order minimum date
        Long minDate = null;
        if (params.containsKey("min_order_date")) {
            String minDateString = params.get("min_order_date");
            if (minDateString.isBlank()) {
                response.put("message", "Field min_order_date can't be blank.");
                return response;
            }
            try {
                minDate = Long.parseLong(minDateString);
            } catch (IllegalArgumentException e) {
                response.put("message", "Field min_order_date must follow the format yyyy-[m]m-[d]d hh:mm:ss[.f...].");
                return response;
            }
        }

        // optional validate order maximum date
        Long maxDate = null;
        if (params.containsKey("max_order_date")) {
            String maxDateString = params.get("max_order_date");
            if (maxDateString.isBlank()) {
                response.put("message", "Field max_order_date can't be blank.");
                return response;
            }
            try {
                maxDate = Long.parseLong(maxDateString);
            } catch (NumberFormatException e) {
                response.put("message", "Field max_order_date must follow the format yyyy-[m]m-[d]d hh:mm:ss[.f...].");
                return response;
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
                response.put("message", "Field min_order_date can't be older than max_order_date.");
                return response;
            }
        }

        // optional validate order payment method
        PaymentMethod paymentMethod = null;
        if (params.containsKey("order_payment_method")) {
            String paymentMethodString = params.get("order_payment_method");
            if (paymentMethodString.isBlank()) {
                response.put("message", "Field order_payment_method can't be blank.");
                return response;
            }
            for (PaymentMethod pm : PaymentMethod.values()) {
                if (paymentMethodString.equals(pm.toString())) {
                    paymentMethod = pm;
                    break;
                }
            }
            if (paymentMethod == null) {
                response.put("message", "Field order_payment_method must be a valid payment method.");
                return response;
            }
        }

        // optional validate order is paid
        Boolean isPaid = null;
        if (params.containsKey("order_is_paid")) {
            String isPaidString = params.get("order_is_paid").toLowerCase();
            if (isPaidString.isBlank()) {
                response.put("message", "Field order_is_paid can't be blank.");
                return response;
            }
            if (!isPaidString.equals("true") && !isPaidString.equals("false")) {
                response.put("message", "Field order_is_paid must be true or false.");
                return response;
            }
            isPaid = Boolean.parseBoolean(isPaidString);
        }

        // optional validate order status
        String status = null;
        if (params.containsKey("order_status")) {
            status = params.get("order_status");
            if (status.isBlank()) {
                response.put("message", "Field order_status can't be blank.");
                return response;
            }
        }

        // optional validate order is fulfilled
        Boolean isFulfilled = null;
        if (params.containsKey("order_is_fulfilled")) {
            String isFulfilledString = params.get("order_is_fulfilled").toLowerCase();
            if (isFulfilledString.isBlank()) {
                response.put("message", "Field order_is_fulfilled can't be blank.");
                return response;
            }
            if (!isFulfilledString.equals("true") && !isFulfilledString.equals("false")) {
                response.put("message", "Field order_is_fulfilled must be true or false.");
                return response;
            }
            isFulfilled = Boolean.parseBoolean(isFulfilledString);
        }

        // optional validate maximum orders per page
        Integer maxOrdersPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                response.put("message", "Field maximum products per page can't be blank.");
                return response;
            }
            try {
                maxOrdersPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                response.put("message", "Field maximum products per page must be a valid integer number.");
                return response;
            }
            if (maxOrdersPerPage < 1) {
                response.put("message", "Field maximum products per page can't be smaller than one.");
                return response;
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                response.put("message", "Field page number can't be blank.");
                return response;
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                response.put("message", "Field page number must be a valid integer number.");
                return response;
            }
            if (pageNumber < 1) {
                response.put("message", "Field page number can't be smaller than one.");
                return response;
            }
        }

        // all validations test passed

        // get orders list
        List<Order> orders = orderService.readOrdersByFilters(client, minDate, maxDate, paymentMethod, isPaid,
                isFulfilled, maxOrdersPerPage, pageNumber);

        response.put("error", false);
        response.put("orders", orders);
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

            i++;
            key = String.format("product_codes[%d]", i);
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
        order.setDate(System.currentTimeMillis());
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
    public HashMap<String, Object> adminUpdateOrder(@RequestParam HashMap<String, String> params) {
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

        // optional validate product codes add list
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

            i++;
            key = String.format("add_product_codes[%d]", i);
        }

        // optional validate product codes remove list
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

            i++;
            key = String.format("rem_product_codes[%d]", i);
        }

        // optional validate charge address
        String chargeAddress = null;
        if (params.containsKey("charge_address")) {
            chargeAddress = params.get("charge_address");
            if (chargeAddress.isBlank()) {
                response.put("message", "Field charge_address can't be blank.");
                return response;
            }
        }

        // optional validate deliver address
        String deliverAddress = null;
        if (params.containsKey("deliver_address")) {
            deliverAddress = params.get("deliver_address");
            if (deliverAddress.isBlank()) {
                response.put("message", "Field deliver_address can't be blank.");
                return response;
            }
        }

        // optional validate paid
        Boolean isPaid = null;
        if (params.containsKey("order_paid")) {
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

        // optional status
        String status = null;
        if (params.containsKey("status")) {
            status = params.get("status");
            if (status.isBlank()) {
                response.put("message", "Field status can't be blank.");
                return response;
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
            response.put("message", "Database couldn't update the order.");
            return response;
        }

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

        // validate that the requested order belongs to the client
        if (order.getClient() != client) {
            // if it doesn't, behave has if the order doesn't even exist
            // because we don't want clients to be able to delete other clients orders
            response.put("message", "Field code must be a valid order code.");
            return response;
        }

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
