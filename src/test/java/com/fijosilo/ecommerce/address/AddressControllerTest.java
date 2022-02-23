package com.fijosilo.ecommerce.address;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
class AddressControllerTest {
    private static Client client;
    private static AddressController addressController;
    private static Authentication authentication;

    @BeforeAll
    static void init() {
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

        client = new Client();
        client.setId(1L);
        client.setFirstName("Lorem");
        client.setLastName("Ipsum");
        client.setEmail("loremipsum@email.com");
        client.setChargeAddress(chargeAddress);
        client.setDeliverAddress(deliverAddress);
        client.setRole("CLIENT");
        client.setEnabled(true);

        ClientService clientService = Mockito.mock(ClientService.class);
        Mockito.when(clientService.readClientByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(clientService.readClientByEmail(Mockito.matches("loremipsum@email.com"))).thenReturn(client);
        Mockito.when(clientService.updateClient(Mockito.any(Client.class))).thenReturn(true);

        AddressService addressService = Mockito.mock(AddressService.class);
        Mockito.when(addressService.createAddress(Mockito.any(Address.class))).thenReturn(true);
        Mockito.when(addressService.updateAddress(Mockito.any(Address.class))).thenReturn(true);
        Mockito.when(addressService.deleteAddress(Mockito.any(Address.class))).thenReturn(true);

        addressController = new AddressController(clientService, addressService);

        authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("loremipsum@email.com");
    }



    @Test
    void readAddressesMethod_isNotAuthenticatedTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.readAddresses(null);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().containsKey("charge_address"));
        assertFalse(response.getBody().containsKey("deliver_address"));
    }

    @Test
    void readAddressesMethod_isAuthenticatedTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.readAddresses(authentication);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("charge_address"));
        assertNotNull(response.getBody().get("charge_address"));
        assertTrue(response.getBody().get("charge_address") instanceof Address);
        assertEquals(client.getChargeAddress(), response.getBody().get("charge_address"));

        assertTrue(response.getBody().containsKey("deliver_address"));
        assertNotNull(response.getBody().get("deliver_address"));
        assertTrue(response.getBody().get("deliver_address") instanceof Address);
        assertEquals(client.getDeliverAddress(), response.getBody().get("deliver_address"));
    }



    @Test
    void createAddressMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createAddressMethod_addressPurposeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_addressPurposeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_addressPurposeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose must be a valid address purpose(CHARGE, DELIVER).", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_firstNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first_name is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_firstNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first_name can't be blank.", response.getBody().get("error"));
    }


    @Test
    void createAddressMethod_firstNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("first_name", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field first_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_lastNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last_name is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_lastNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_lastNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("last_name", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field last_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_streetIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field street is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_streetIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field street can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_numberIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_numberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_numberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_numberIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number can't be negative.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_apartmentIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field apartment can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_apartmentIsValidFormatTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"DT3", "3 DT 1"};
        for (String s : invalidNames) {
            params.put("apartment", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_postalCodeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field postal_code is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_postalCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field postal_code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_postalCodeIsValidFormatTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"123-4567", "1234567"};
        for (String s : invalidNames) {
            params.put("postal_code", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field postal_code must follow the format 'DDDD-DDD' where D is a digit.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_localityIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field locality is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_localityIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field locality can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_countryIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field country is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_countryIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field country can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_taxNumberIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field tax_number is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_taxNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field tax_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_taxNumberContainsOnlyDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"A23456789", "12345678?"};
        for (String s : invalidNames) {
            params.put("tax_number", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field tax_number must contain only digits.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_taxNumberHasNineDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"12345678", "12345678910"};
        for (String s : invalidNames) {
            params.put("tax_number", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field tax_number must have nine digits.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_phoneNumberIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field phone_number is required.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_phoneNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");
        params.put("phone_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field phone_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createAddressMethod_phoneNumberContainsOnlyDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"A23456789", "12345678?"};
        for (String s : invalidNames) {
            params.put("phone_number", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field phone_number must contain only digits.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_phoneNumberHasNineDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"12345678", "12345678910"};
        for (String s : invalidNames) {
            params.put("phone_number", s);

            response = addressController.createAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field phone_number must have nine digits.", response.getBody().get("error"));
        }
    }

    @Test
    void createAddressMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");
        params.put("phone_number", "987654321");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.createAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }



    @Test
    void updateAddressMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void updateAddressMethod_addressPurposeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose is required.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_addressPurposeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_addressPurposeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose must be a valid address purpose(CHARGE, DELIVER).", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_isAuthenticatedNoOptionalParametersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAddressMethod_firstNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first_name can't be blank.", response.getBody().get("error"));
    }


    @Test
    void updateAddressMethod_firstNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("first_name", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field first_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_lastNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("last_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_lastNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("last_name", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field last_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_streetIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("street", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field street can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_numberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_numberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("number", "3.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_numberIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("number", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field number can't be negative.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_apartmentIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("apartment", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field apartment can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_apartmentIsValidFormatTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"DT3", "3 DT 1"};
        for (String s : invalidNames) {
            params.put("apartment", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_postalCodeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("postal_code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field postal_code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_postalCodeIsValidFormatTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"123-4567", "1234567"};
        for (String s : invalidNames) {
            params.put("postal_code", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field postal_code must follow the format 'DDDD-DDD' where D is a digit.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_localityIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("locality", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field locality can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_countryIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("country", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field country can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_taxNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("tax_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field tax_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_taxNumberContainsOnlyDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"A23456789", "12345678?"};
        for (String s : invalidNames) {
            params.put("tax_number", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field tax_number must contain only digits.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_taxNumberHasNineDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"12345678", "12345678910"};
        for (String s : invalidNames) {
            params.put("tax_number", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field tax_number must have nine digits.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_phoneNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("phone_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field phone_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateAddressMethod_phoneNumberContainsOnlyDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"A23456789", "12345678?"};
        for (String s : invalidNames) {
            params.put("phone_number", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field phone_number must contain only digits.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_phoneNumberHasNineDigitsTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"12345678", "12345678910"};
        for (String s : invalidNames) {
            params.put("phone_number", s);

            response = addressController.updateAddress(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field phone_number must have nine digits.", response.getBody().get("error"));
        }
    }

    @Test
    void updateAddressMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "CHARGE");
        params.put("first_name", "Orange");
        params.put("last_name", "Black");
        params.put("street", "Color Street");
        params.put("number", "3");
        params.put("apartment", "1 DT");
        params.put("postal_code", "1234-123");
        params.put("locality", "Colorland");
        params.put("country", "Portugal");
        params.put("tax_number", "123456789");
        params.put("phone_number", "987654321");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.updateAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void deleteAddressMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.deleteAddress(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void deleteAddressMethod_addressPurposeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.deleteAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose is required.", response.getBody().get("error"));
    }

    @Test
    void deleteAddressMethod_addressPurposeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.deleteAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose can't be blank.", response.getBody().get("error"));
    }

    @Test
    void deleteAddressMethod_addressPurposeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.deleteAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field address_purpose must be a valid address purpose(CHARGE, DELIVER).", response.getBody().get("error"));
    }

    @Test
    void deleteAddressMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("address_purpose", "DELIVER");

        // response
        ResponseEntity<HashMap<String, Object>> response = addressController.deleteAddress(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(client.getDeliverAddress());
    }

}
