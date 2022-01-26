package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.address.Address;
import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import com.fijosilo.ecommerce.product.Product;
import com.fijosilo.ecommerce.product.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {
    private static OrderController orderController;
    private static Authentication authentication, authenticationInvalidChargeAddress, authenticationInvalidDeliverAddress;
    private static Client clientValid, clientInvalidChargeAddress, clientInvalidDeliverAddress;
    private static Order orderOwned, orderNotOwned;
    private static List<Order> ordersOwned, orders;

    @BeforeAll
    static void init() {
        // client service

        Address chargeAddress = new Address();
        chargeAddress.setFirstName("Ipsum");
        chargeAddress.setLastName("Lorem");
        chargeAddress.setStreet("Rua do Texto");
        chargeAddress.setNumber(1);
        chargeAddress.setApartment("3 DT");
        chargeAddress.setPostalCode("1234-567");
        chargeAddress.setLocality("Livroterra");
        chargeAddress.setCountry("Portugal");
        chargeAddress.setTaxNumber("123456789");
        chargeAddress.setPhoneNumber("987654321");

        Address deliverAddress = new Address();
        deliverAddress.setFirstName("Lorem");
        deliverAddress.setLastName("Ipsum");
        deliverAddress.setStreet("Rua do Texto");
        deliverAddress.setNumber(1);
        deliverAddress.setApartment("3 ED");
        deliverAddress.setPostalCode("1234-567");
        deliverAddress.setLocality("Livroterra");
        deliverAddress.setCountry("Portugal");
        deliverAddress.setTaxNumber("987654321");
        deliverAddress.setPhoneNumber("123456789");

        clientValid = new Client();
        clientValid.setId(1L);
        clientValid.setFirstName("Lorem");
        clientValid.setLastName("Ipsum");
        clientValid.setEmail("loremipsum@email.com");
        clientValid.setChargeAddress(chargeAddress);
        clientValid.setDeliverAddress(deliverAddress);
        clientValid.setRole("CLIENT");
        clientValid.setEnabled(true);
        // authentication
        authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("loremipsum@email.com");

        clientInvalidChargeAddress = new Client();
        clientInvalidChargeAddress.setId(1L);
        clientInvalidChargeAddress.setFirstName("Invalid");
        clientInvalidChargeAddress.setLastName("Charge");
        clientInvalidChargeAddress.setEmail("invalidchargeaddress@email.com");
        clientInvalidChargeAddress.setChargeAddress(null);
        clientInvalidChargeAddress.setDeliverAddress(deliverAddress);
        clientInvalidChargeAddress.setRole("CLIENT");
        clientInvalidChargeAddress.setEnabled(true);
        // authentication
        authenticationInvalidChargeAddress = Mockito.mock(Authentication.class);
        Mockito.when(authenticationInvalidChargeAddress.getName()).thenReturn("invalidchargeaddress@email.com");

        clientInvalidDeliverAddress = new Client();
        clientInvalidDeliverAddress.setId(1L);
        clientInvalidDeliverAddress.setFirstName("Invalid");
        clientInvalidDeliverAddress.setLastName("Deliver");
        clientInvalidDeliverAddress.setEmail("invaliddeliveraddress@email.com");
        clientInvalidDeliverAddress.setChargeAddress(chargeAddress);
        clientInvalidDeliverAddress.setDeliverAddress(null);
        clientInvalidDeliverAddress.setRole("CLIENT");
        clientInvalidDeliverAddress.setEnabled(true);
        // authentication
        authenticationInvalidDeliverAddress = Mockito.mock(Authentication.class);
        Mockito.when(authenticationInvalidDeliverAddress.getName()).thenReturn("invaliddeliveraddress@email.com");

        ClientService clientService = Mockito.mock(ClientService.class);
        Mockito.when(clientService.readClientByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(clientService.readClientByEmail(Mockito.matches("loremipsum@email.com"))).thenReturn(clientValid);
        Mockito.when(clientService.readClientByEmail(Mockito.matches("invalidchargeaddress@email.com"))).thenReturn(clientInvalidChargeAddress);
        Mockito.when(clientService.readClientByEmail(Mockito.matches("invaliddeliveraddress@email.com"))).thenReturn(clientInvalidDeliverAddress);

        // product service

        Product productPhone = new Product();
        productPhone.setId(1L);
        productPhone.setCode("QS1642517236929");
        productPhone.setProductBrand(null);
        productPhone.setName("Quasar Smartphone");
        productPhone.setDescription("A fictitious smartphone for testing purposes");
        productPhone.setPrice(199.99);
        productPhone.setStock(10);
        productPhone.setThumbnailURL(null);
        productPhone.setDiscount(0.0);
        productPhone.setImagesURL(null);
        productPhone.setAdditionDate(1642517236929L);
        productPhone.setEnabled(true);

        Product productTv = new Product();
        productTv.setId(1L);
        productTv.setCode("QT1642517235938");
        productTv.setProductBrand(null);
        productTv.setName("Quasar Tv");
        productTv.setDescription("A fictitious tv for testing purposes");
        productTv.setPrice(499.99);
        productTv.setStock(10);
        productTv.setThumbnailURL(null);
        productTv.setDiscount(0.0);
        productTv.setImagesURL(null);
        productTv.setAdditionDate(1642517236929L);
        productTv.setEnabled(true);

        ProductService productService = Mockito.mock(ProductService.class);
        Mockito.when(productService.readProductByCode(Mockito.anyString())).thenReturn(null);
        Mockito.when(productService.readProductByCode(Mockito.matches("QS1642517236929"))).thenReturn(productPhone);
        Mockito.when(productService.readProductByCode(Mockito.matches("QT1642517235938"))).thenReturn(productTv);

        // order service

        orderOwned = new Order();
        orderOwned.setId(1L);
        orderOwned.setCode("9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        orderOwned.setClient(clientValid);
        orderOwned.setDate(1643128092458L);
        orderOwned.setChargeAddress(chargeAddress.toString());
        orderOwned.setPaymentMethod("PAYPAL");
        orderOwned.setPaid(false);
        orderOwned.setDeliverAddress(deliverAddress.toString());
        orderOwned.setStatus("Waiting for payment");
        orderOwned.setFulfilled(false);

        orderNotOwned = new Order();
        orderNotOwned.setId(2L);
        orderNotOwned.setCode("9a3e484a-7dfb-11ec-90d6-0242ac120003");
        orderNotOwned.setClient(null);
        orderNotOwned.setDate(1643128093443L);
        orderNotOwned.setChargeAddress(null);
        orderNotOwned.setPaymentMethod("PAYPAL");
        orderNotOwned.setPaid(false);
        orderNotOwned.setDeliverAddress(null);
        orderNotOwned.setStatus("Waiting for payment");
        orderNotOwned.setFulfilled(false);

        ordersOwned = new LinkedList<>();
        ordersOwned.add(orderOwned);

        orders = new LinkedList<>();
        orders.add(orderOwned);
        orders.add(orderNotOwned);

        OrderService orderService = Mockito.mock(OrderService.class);
        Mockito.when(orderService.readOrderByCode(Mockito.anyString())).thenReturn(null);
        Mockito.when(orderService.readOrderByCode(Mockito.matches("9a3e45d4-7dfb-11ec-90d6-0242ac120003"))).thenReturn(orderOwned);
        Mockito.when(orderService.readOrderByCode(Mockito.matches("9a3e484a-7dfb-11ec-90d6-0242ac120003"))).thenReturn(orderNotOwned);
        Mockito.when(orderService.readOrdersByClient(Mockito.any(Client.class),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(new LinkedList<>());
        Mockito.when(orderService.readOrdersByClient(Mockito.eq(clientValid),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(ordersOwned);
        Mockito.when(orderService.readOrdersByFilters(Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(orders);
        Mockito.when(orderService.readOrdersByFilters(Mockito.eq(clientValid),
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any(PaymentMethod.class),
                Mockito.anyBoolean(),
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(ordersOwned);
        Mockito.when(orderService.createOrder(Mockito.any(Order.class))).thenReturn(true);
        Mockito.when(orderService.updateOrder(Mockito.any(Order.class))).thenReturn(true);
        Mockito.when(orderService.deleteOrder(Mockito.any(Order.class))).thenReturn(true);

        // order controller
        orderController = new OrderController(orderService, clientService, productService);
    }



    @Test
    void readOrderMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void readOrderMethod_orderCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void readOrderMethod_orderCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readOrderMethod_orderCodeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void readOrderMethod_orderBelongsToClientTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e484a-7dfb-11ec-90d6-0242ac120003");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("An order with the code provided was not found.", response.getBody().get("error"));
    }

    @Test
    void readOrderMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("order"));
        assertNotNull(response.getBody().get("order"));
        assertTrue(response.getBody().get("order") instanceof Order);
        assertEquals(orderOwned, response.getBody().get("order"));
    }



    @Test
    void adminReadOrderMethod_orderCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrderMethod_orderCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrderMethod_orderCodeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrderMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e484a-7dfb-11ec-90d6-0242ac120003");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrder(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("order"));
        assertNotNull(response.getBody().get("order"));
        assertTrue(response.getBody().get("order") instanceof Order);
        assertEquals(orderNotOwned, response.getBody().get("order"));
    }



    @Test
    void readOrdersMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void readOrdersMethod_maxOrdersPerPageIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_maxOrdersPerPageIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_maxOrdersPerPageIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_pageNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_pageNumberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_pageNumberIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readOrdersMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "10");
        params.put("page_number", "1");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.readOrders(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("orders"));
        assertNotNull(response.getBody().get("orders"));
        assertTrue(response.getBody().get("orders") instanceof List);
        assertEquals(ordersOwned, response.getBody().get("orders"));
    }



    @Test
    void adminReadOrdersMethod_clientEmailIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("client_email", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field client_email can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_clientEmailIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("client_email", "clientdoesnotexist@email.com");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field client_email must be a valid client email.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_minOrderDateIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("min_order_date", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field min_order_date can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_minOrderDateIsValidLongTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidDates = new String[]{"1900-01-01", "00:00:00", "1 Nov 1900", "1900-01-01 00:00:00", "164313.7559472"};
        for (String s : invalidDates) {
            params.put("min_order_date", s);

            response = orderController.adminReadOrders(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field min_order_date must be a valid integer number.", response.getBody().get("error"));
        }
    }

    @Test
    void adminReadOrdersMethod_maxOrderDateIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_order_date", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_order_date can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_maxOrderDateIsValidLongTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidDates = new String[]{"1900-01-01", "00:00:00", "1 Nov 1900", "1900-01-01 00:00:00", "164313.7559472"};
        for (String s : invalidDates) {
            params.put("max_order_date", s);

            response = orderController.adminReadOrders(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field max_order_date must be a valid integer number.", response.getBody().get("error"));
        }
    }

    @Test
    void adminReadOrdersMethod_maxOrderDateIsOlderThanMinOrderDateTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("min_order_date", "1643137559472");
        params.put("max_order_date", "1643137559470");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field min_order_date can't be older than max_order_date.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_paymentMethodIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("order_payment_method", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_payment_method can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_paymentMethodIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("order_payment_method", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_payment_method must be a valid payment method.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_orderIsPaidIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("order_is_paid", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_is_paid can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_orderStatusIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("order_status", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_status can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_orderIsFulfilledIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("order_is_fulfilled", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_is_fulfilled can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_maxOrdersPerPageIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_maxOrdersPerPageIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_maxOrdersPerPageIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_orders_per_page", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_orders_per_page can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_pageNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_pageNumberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_pageNumberIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void adminReadOrdersMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminReadOrders(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("orders"));
        assertNotNull(response.getBody().get("orders"));
        assertTrue(response.getBody().get("orders") instanceof List);
        assertEquals(orders, response.getBody().get("orders"));
    }



    @Test
    void createOrderMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createOrderMethod_clientHasValidChargeAddressTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authenticationInvalidChargeAddress, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Client needs to have a charge address defined.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_clientHasValidDeliverAddressTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authenticationInvalidDeliverAddress, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Client needs to have a deliver address defined.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_productCodesAreRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field product_codes is required.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_productCodesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field product_codes[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_productCodesAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field product_codes[1] must be a valid product code.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_paymentMethodIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "QT1642517235938");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field payment_method is required.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_paymentMethodIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "QT1642517235938");
        params.put("payment_method", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field payment_method can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_paymentMethodIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "QT1642517235938");
        params.put("payment_method", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field payment_method must be a valid payment method.", response.getBody().get("error"));
    }

    @Test
    void createOrderMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("product_codes[0]", "QS1642517236929");
        params.put("product_codes[1]", "QT1642517235938");
        params.put("payment_method", "PAYPAL");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.createOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }



    @Test
    void adminUpdateOrderMethod_orderCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_orderCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_orderCodeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_addProductCodesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("add_product_codes[0]", "QS1642517236929");
        params.put("add_product_codes[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field add_product_codes[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_addProductCodesAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("add_product_codes[0]", "QS1642517236929");
        params.put("add_product_codes[1]", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field add_product_codes[1] must be a valid product code.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_remProductCodesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("rem_product_codes[0]", "QT1642517235938");
        params.put("rem_product_codes[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field rem_product_codes[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_remProductCodesAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("rem_product_codes[0]", "QT1642517235938");
        params.put("rem_product_codes[1]", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field rem_product_codes[1] must be a valid product code.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_chargeAddressIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("charge_address", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field charge_address can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_deliverAddressIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("deliver_address", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field deliver_address can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_orderPaidIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("order_paid", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field order_paid can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_statusIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("status", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field status can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminUpdateOrderMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "9a3e45d4-7dfb-11ec-90d6-0242ac120003");
        params.put("add_product_codes[0]", "QS1642517236929");
        params.put("rem_product_codes[1]", "QT1642517235938");
        params.put("charge_address", clientValid.getChargeAddress().toString());
        params.put("deliver_address", clientValid.getDeliverAddress().toString());
        params.put("order_paid", "true");
        params.put("status", "Processing");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminUpdateOrder(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void deleteOrderMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void deleteOrderMethod_orderCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void deleteOrderMethod_orderCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void deleteOrderMethod_orderCodeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void deleteOrderMethod_orderCodeBelongsToClientTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", orderNotOwned.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void deleteOrderMethod_orderIsAlreadyPaidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", orderOwned.getCode());

        // response
        orderOwned.setPaid(true);
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);
        orderOwned.setPaid(false);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Order has already been paid. Contact the support to cancel your order", response.getBody().get("error"));
    }

    @Test
    void deleteOrderMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", orderOwned.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.deleteOrder(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void adminDeleteOrderMethod_orderCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminDeleteOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void adminDeleteOrderMethod_orderCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminDeleteOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void adminDeleteOrderMethod_orderCodeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminDeleteOrder(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid order code.", response.getBody().get("error"));
    }

    @Test
    void adminDeleteOrderMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", orderNotOwned.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response = orderController.adminDeleteOrder(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
