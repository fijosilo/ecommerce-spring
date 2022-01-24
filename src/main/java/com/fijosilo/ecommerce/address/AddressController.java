package com.fijosilo.ecommerce.address;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class AddressController {
    private final ClientService clientService;
    private final AddressService addressService;

    public AddressController (ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    // read the addresses of the current(logged in) client
    @GetMapping(value = "/client/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readAddresses(Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        if (authentication == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }
        Client client = clientService.readClientByEmail(authentication.getName());

        // return the charge and deliver addresses
        payload.put("charge_address", client.getChargeAddress());
        payload.put("deliver_address", client.getDeliverAddress());
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    // creates and assigns a new charge or deliver address to current(logged in) the client
    @PostMapping(value = "/client/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> createAddress(Authentication authentication, @RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        if (authentication == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }
        Client client = clientService.readClientByEmail(authentication.getName());

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            payload.put("error", "Field address_purpose is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            payload.put("error", "Field address_purpose can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            payload.put("error", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate firstName
        if (!params.containsKey("first_name")) {
            payload.put("error", "Field first_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String firstName = params.get("first_name").toLowerCase();
        if (firstName.isBlank()) {
            payload.put("error", "Field first_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!firstName.matches("\\p{L}+")) {
            payload.put("error", "Field first_name can contain only letters.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);

        // validate lastName
        if (!params.containsKey("last_name")) {
            payload.put("error", "Field last_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String lastName = params.get("last_name").toLowerCase();
        if (lastName.isBlank()) {
            payload.put("error", "Field last_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!lastName.matches("\\p{L}+")) {
            payload.put("error", "Field last_name can contain only letters.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

        // validate street
        if (!params.containsKey("street")) {
            payload.put("error", "Field street is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String street = params.get("street");
        if (street.isBlank()) {
            payload.put("error", "Field street can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate number
        if (!params.containsKey("number")) {
            payload.put("error", "Field number is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String numberString = params.get("number");
        if (numberString.isBlank()) {
            payload.put("error", "Field number can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Integer number = null;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            payload.put("error", "Field number must be a valid integer number.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (number < 0) {
            payload.put("error", "Field number can't be negative.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate apartment
        String apartment = null;
        if (params.containsKey("apartment")) {
            apartment = params.get("apartment").replaceAll("\\p{Z}", "").toUpperCase();
            if (apartment.isBlank()) {
                payload.put("error", "Field apartment can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!apartment.matches("\\p{N}+\\p{L}{0,2}")) {
                payload.put("error", "Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate postalCode
        if (!params.containsKey("postal_code")) {
            payload.put("error", "Field postal_code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String postalCode = params.get("postal_code").replaceAll("\\p{Z}", "");
        if (postalCode.isBlank()) {
            payload.put("error", "Field postal_code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!postalCode.matches("\\p{N}{4}[-]\\p{N}{3}")) {
            payload.put("error", "Field postal_code must follow the format 'DDDD-DDD' where D is a digit.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate locality
        if (!params.containsKey("locality")) {
            payload.put("error", "Field locality is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String locality = params.get("locality");
        if (locality.isBlank()) {
            payload.put("error", "Field locality can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate country
        if (!params.containsKey("country")) {
            payload.put("error", "Field country is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String country = params.get("country");
        if (country.isBlank()) {
            payload.put("error", "Field country can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate taxNumber
        if (!params.containsKey("tax_number")) {
            payload.put("error", "Field tax_number is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String taxNumber = params.get("tax_number").replaceAll("\\p{Z}", "");
        if (taxNumber.isBlank()) {
            payload.put("error", "Field tax_number can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!taxNumber.matches("\\p{N}*")) {
            payload.put("error", "Field tax_number must contain only digits.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (taxNumber.length() != 9) {
            payload.put("error", "Field tax_number must have nine digits.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate phoneNumber
        if (!params.containsKey("phone_number")) {
            payload.put("error", "Field phone_number is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String phoneNumber = params.get("phone_number").replaceAll("\\p{Z}", "");
        if (phoneNumber.isBlank()) {
            payload.put("error", "Field phone_number can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!phoneNumber.matches("\\p{N}*")) {
            payload.put("error", "Field phone_number must contain only digits.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (phoneNumber.length() != 9) {
            payload.put("error", "Field phone_number must have nine digits.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validation tests passed

        // create the new address
        Address address = new Address();
        address.setFirstName(firstName);
        address.setLastName(lastName);
        address.setStreet(street);
        address.setNumber(number);
        address.setApartment(apartment);
        address.setPostalCode(postalCode);
        address.setLocality(locality);
        address.setCountry(country);
        address.setTaxNumber(taxNumber);
        address.setPhoneNumber(phoneNumber);
        addressService.createAddress(address);

        // update current address
        switch (addressPurpose) {
            case CHARGE:
                client.setChargeAddress(address);
                break;
            case DELIVER:
                client.setDeliverAddress(address);
                break;
            default:
                break;
        }
        clientService.updateClient(client);

        // return response
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    // update client address of type if current(logged in) client address of same type is not null
    @PutMapping(value = "/client/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> updateAddress(Authentication authentication, @RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        if (authentication == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }
        Client client = clientService.readClientByEmail(authentication.getName());

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            payload.put("error", "Field address_purpose is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            payload.put("error", "Field address_purpose can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            payload.put("error", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate address
        Address address = null;
        switch (addressPurpose) {
            case CHARGE:
                address = client.getChargeAddress();
                break;
            case DELIVER:
                address = client.getDeliverAddress();
                break;
            default:
                break;
        }
        if (address == null) {
            payload.put("error", "The specified client address was not created yet, create it first.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate firstName
        String firstName = null;
        if (params.containsKey("first_name")) {
            firstName = params.get("first_name").toLowerCase();
            if (firstName.isBlank()) {
                payload.put("error", "Field first_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!firstName.matches("\\p{L}+")) {
                payload.put("error", "Field first_name can contain only letters.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        }

        // validate lastName
        String lastName = null;
        if (params.containsKey("last_name")) {
            lastName = params.get("last_name").toLowerCase();
            if (lastName.isBlank()) {
                payload.put("error", "Field last_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!lastName.matches("\\p{L}+")) {
                payload.put("error", "Field last_name can contain only letters.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        }

        // validate street
        String street = null;
        if (params.containsKey("street")) {
            street = params.get("street");
            if (street.isBlank()) {
                payload.put("error", "Field street can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate number
        Integer number = null;
        if (params.containsKey("number")) {
            String numberString = params.get("number");
            if (numberString.isBlank()) {
                payload.put("error", "Field number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                number = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (number < 0) {
                payload.put("error", "Field number can't be negative.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate apartment
        String apartment = null;
        if (params.containsKey("apartment")) {
            apartment = params.get("apartment").replaceAll("\\p{Z}", "").toUpperCase();
            if (apartment.isBlank()) {
                payload.put("error", "Field apartment can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!apartment.matches("\\p{N}+\\p{L}{0,2}")) {
                payload.put("error", "Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate postalCode
        String postalCode = null;
        if (params.containsKey("postal_code")) {
            postalCode = params.get("postal_code").replaceAll("\\p{Z}", "");
            if (postalCode.isBlank()) {
                payload.put("error", "Field postal_code can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!postalCode.matches("\\p{N}{4}[-]\\p{N}{3}")) {
                payload.put("error", "Field postal_code must follow the format 'DDDD-DDD' where D is a digit.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate locality
        String locality = null;
        if (params.containsKey("locality")) {
            locality = params.get("locality");
            if (locality.isBlank()) {
                payload.put("error", "Field locality can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate country
        String country = null;
        if (params.containsKey("country")) {
            country = params.get("country");
            if (country.isBlank()) {
                payload.put("error", "Field country can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate taxNumber
        String taxNumber = null;
        if (params.containsKey("tax_number")) {
            taxNumber = params.get("tax_number").replaceAll("\\p{Z}", "");
            if (taxNumber.isBlank()) {
                payload.put("error", "Field tax_number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!taxNumber.matches("\\p{N}*")) {
                payload.put("error", "Field tax_number must contain only digits.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (taxNumber.length() != 9) {
                payload.put("error", "Field tax_number must have nine digits.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // validate phoneNumber
        String phoneNumber = null;
        if (params.containsKey("phone_number")) {
            phoneNumber = params.get("phone_number").replaceAll("\\p{Z}", "");
            if (phoneNumber.isBlank()) {
                payload.put("error", "Field phone_number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!phoneNumber.matches("\\p{N}*")) {
                payload.put("error", "Field phone_number must contain only digits.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (phoneNumber.length() != 9) {
                payload.put("error", "Field phone_number must have nine digits.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validation tests passed

        // update the address
        if (firstName != null) address.setFirstName(firstName);
        if (lastName != null) address.setLastName(lastName);
        if (street != null) address.setStreet(street);
        if (number != null) address.setNumber(number);
        if (apartment != null) address.setApartment(apartment);
        if (postalCode != null) address.setPostalCode(postalCode);
        if (locality != null) address.setLocality(locality);
        if (country != null) address.setCountry(country);
        if (taxNumber != null) address.setTaxNumber(taxNumber);
        if (phoneNumber != null) address.setPhoneNumber(phoneNumber);
        addressService.updateAddress(address);

        // return response
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    // delete(set to null) client address of type of the current(logged in) client
    @DeleteMapping(value = "/client/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> deleteAddress(Authentication authentication, @RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        if (authentication == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }
        Client client = clientService.readClientByEmail(authentication.getName());

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            payload.put("error", "Field address_purpose is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            payload.put("error", "Field address_purpose can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate address
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            payload.put("error", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validation tests passed

        // delete address
        Address address = null;
        switch (addressPurpose) {
            case CHARGE:
                address = client.getChargeAddress();
                client.setChargeAddress(null);
                break;
            case DELIVER:
                address = client.getDeliverAddress();
                client.setDeliverAddress(null);
                break;
            default:
                break;
        }
        addressService.deleteAddress(address);

        // return response
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
