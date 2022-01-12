package com.fijosilo.ecommerce.address;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class AddressController {
    private final ClientService clientService;
    private final AddressService addressService;

    public AddressController (ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    // read the addresses of the current(loged in) client
    @GetMapping("/client/addresses")
    public HashMap<String, Object> readAddresses(Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("error", true);
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // return the charge and deliver addresses
        response.put("error", false);
        response.put("charge_address", client.getChargeAddress());
        response.put("deliver_address", client.getDeliverAddress());
        return response;
    }

    // creates and assigns a new charge or deliver address to current(loged in) the client
    @PostMapping("/client/address")
    public HashMap<String, Object> createAddress(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            response.put("message", "Field address_purpose is required.");
            return response;
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            response.put("message", "Field address_purpose can't be blank.");
            return response;
        }
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            response.put("message", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return response;
        }

        // validate firstName
        if (!params.containsKey("first_name")) {
            response.put("message", "Field first_name is required.");
            return response;
        }
        String firstName = params.get("first_name").toLowerCase();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        if (firstName.isBlank()) {
            response.put("message", "Field first_name can't be blank.");
            return response;
        }
        if (!firstName.matches("\\p{L}+")) {
            response.put("message", "Field first_name can contain only letters.");
            return response;
        }

        // validate lastName
        if (!params.containsKey("last_name")) {
            response.put("message", "Field last_name is required.");
            return response;
        }
        String lastName = params.get("last_name").toLowerCase();
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        if (lastName.isBlank()) {
            response.put("message", "Field last_name can't be blank.");
            return response;
        }
        if (!lastName.matches("\\p{L}+")) {
            response.put("message", "Field last_name can contain only letters.");
            return response;
        }

        // validate street
        if (!params.containsKey("street")) {
            response.put("message", "Field street is required.");
            return response;
        }
        String street = params.get("street");
        if (street.isBlank()) {
            response.put("message", "Field street can't be blank.");
            return response;
        }

        // validate number
        if (!params.containsKey("number")) {
            response.put("message", "Field number is required.");
            return response;
        }
        String numberString = params.get("number");
        if (numberString.isBlank()) {
            response.put("message", "Field number can't be blank.");
            return response;
        }
        Integer number = null;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            response.put("message", "Field number must be a valid integer number.");
            return response;
        }
        if (number < 0) {
            response.put("message", "Field number can't be negative.");
            return response;
        }

        // optional validate apartment
        String apartment = null;
        if (params.containsKey("apartment")) {
            apartment = params.get("apartment").replaceAll("\\p{Z}", "").toUpperCase();
            if (apartment.isBlank()) {
                response.put("message", "Field apartment can't be blank.");
                return response;
            }
            if (!apartment.matches("\\p{N}+\\p{L}{0,2}")) {
                response.put("message", "Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.");
                return response;
            }
        }

        // validate postalCode
        if (!params.containsKey("postal_code")) {
            response.put("message", "Field postal_code is required.");
            return response;
        }
        String postalCode = params.get("postal_code").replaceAll("\\p{Z}", "");
        if (postalCode.isBlank()) {
            response.put("message", "Field postal_code can't be blank.");
            return response;
        }
        if (!postalCode.matches("\\p{N}{4}[-]\\p{N}{3}")) {
            response.put("message", "Field postal_code must follow the format 'DDDD-DDD' where D is a digit.");
            return response;
        }

        // validate locality
        if (!params.containsKey("locality")) {
            response.put("message", "Field locality is required.");
            return response;
        }
        String locality = params.get("locality");
        if (locality.isBlank()) {
            response.put("message", "Field locality can't be blank.");
            return response;
        }

        // validate country
        if (!params.containsKey("country")) {
            response.put("message", "Field country is required.");
            return response;
        }
        String country = params.get("country");
        if (country.isBlank()) {
            response.put("message", "Field country can't be blank.");
            return response;
        }

        // validate taxNumber
        if (!params.containsKey("tax_number")) {
            response.put("message", "Field tax_number is required.");
            return response;
        }
        String taxNumber = params.get("tax_number").replaceAll("\\p{Z}", "");
        if (taxNumber.isBlank()) {
            response.put("message", "Field tax_number can't be blank.");
            return response;
        }
        if (!taxNumber.matches("\\p{N}*")) {
            response.put("message", "Field tax_number must contain only digits.");
            return response;
        }
        if (taxNumber.length() != 9) {
            response.put("message", "Field tax_number must contain nine digits.");
            return response;
        }

        // validate phoneNumber
        if (!params.containsKey("phone_number")) {
            response.put("message", "Field phone_number is required.");
            return response;
        }
        String phoneNumber = params.get("phone_number").replaceAll("\\p{Z}", "");
        if (phoneNumber.isBlank()) {
            response.put("message", "Field phone_number can't be blank.");
            return response;
        }
        if (!phoneNumber.matches("\\p{N}*")) {
            response.put("message", "Field phone_number must contain only digits.");
            return response;
        }
        if (phoneNumber.length() != 9) {
            response.put("message", "Field phone_number must contain nine digits.");
            return response;
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

        //return response
        response.put("error", false);
        return response;
    }

    // update client address of type if current(loged in) client address of same type is not null
    @PutMapping("/client/address")
    public HashMap<String, Object> updateAddress(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            response.put("message", "Field address_purpose is required.");
            return response;
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            response.put("message", "Field address_purpose can't be blank.");
            return response;
        }
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            response.put("message", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return response;
        }
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
            response.put("message", "The specified client address was not created yet, create it first.");
            return response;
        }

        // validate firstName
        String firstName = null;
        if (params.containsKey("first_name")) {
            firstName = params.get("first_name").toLowerCase();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            if (firstName.isBlank()) {
                response.put("message", "Field first_name can't be blank.");
                return response;
            }
            if (!firstName.matches("\\p{L}+")) {
                response.put("message", "Field first_name can contain only letters.");
                return response;
            }
        }

        // validate lastName
        String lastName = null;
        if (params.containsKey("last_name")) {
            lastName = params.get("last_name").toLowerCase();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
            if (lastName.isBlank()) {
                response.put("message", "Field last_name can't be blank.");
                return response;
            }
            if (!lastName.matches("\\p{L}+")) {
                response.put("message", "Field last_name can contain only letters.");
                return response;
            }
        }

        // validate street
        String street = null;
        if (params.containsKey("street")) {
            street = params.get("street");
            if (street.isBlank()) {
                response.put("message", "Field street can't be blank.");
                return response;
            }
        }

        // optional validate number
        Integer number = null;
        if (params.containsKey("number")) {
            String numberString = params.get("number");
            if (numberString.isBlank()) {
                response.put("message", "Field number can't be blank.");
                return response;
            }
            try {
                number = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                response.put("message", "Field number must be a valid integer number.");
                return response;
            }
            if (number < 0) {
                response.put("message", "Field number can't be negative.");
                return response;
            }
        }

        // validate apartment
        String apartment = null;
        if (params.containsKey("apartment")) {
            apartment = params.get("apartment").replaceAll("\\p{Z}", "").toUpperCase();
            if (apartment.isBlank()) {
                response.put("message", "Field apartment can't be blank.");
                return response;
            }
            if (!apartment.matches("\\p{N}+\\p{L}{0,2}")) {
                response.put("message", "Field apartment must follow the format 'N' or 'N LL' where N is a number and L is a letter.");
                return response;
            }
        }

        // validate postalCode
        String postalCode = null;
        if (params.containsKey("postal_code")) {
            postalCode = params.get("postal_code").replaceAll("\\p{Z}", "");
            if (postalCode.isBlank()) {
                response.put("message", "Field postal_code can't be blank.");
                return response;
            }
            if (!postalCode.matches("\\p{N}{4}[-]\\p{N}{3}")) {
                response.put("message", "Field postal_code must follow the format 'DDDD-DDD' where D is a digit.");
                return response;
            }
        }

        // validate locality
        String locality = null;
        if (params.containsKey("locality")) {
            locality = params.get("locality");
            if (locality.isBlank()) {
                response.put("message", "Field locality can't be blank.");
                return response;
            }
        }

        // validate country
        String country = null;
        if (params.containsKey("country")) {
            country = params.get("country");
            if (country.isBlank()) {
                response.put("message", "Field country can't be blank.");
                return response;
            }
        }

        // validate taxNumber
        String taxNumber = null;
        if (params.containsKey("tax_number")) {
            taxNumber = params.get("tax_number").replaceAll("\\p{Z}", "");
            if (taxNumber.isBlank()) {
                response.put("message", "Field tax_number can't be blank.");
                return response;
            }
            if (!taxNumber.matches("\\p{N}*")) {
                response.put("message", "Field tax_number must contain only digits.");
                return response;
            }
            if (taxNumber.length() != 9) {
                response.put("message", "Field tax_number must contain nine digits.");
                return response;
            }
        }

        // validate phoneNumber
        String phoneNumber = null;
        if (params.containsKey("phone_number")) {
            phoneNumber = params.get("phone_number").replaceAll("\\p{Z}", "");
            if (phoneNumber.isBlank()) {
                response.put("message", "Field phone_number can't be blank.");
                return response;
            }
            if (!phoneNumber.matches("\\p{N}*")) {
                response.put("message", "Field phone_number must contain only digits.");
                return response;
            }
            if (phoneNumber.length() != 9) {
                response.put("message", "Field phone_number must contain nine digits.");
                return response;
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

        //return response
        response.put("error", false);
        return response;
    }

    // delete(set to null) client address of type of the current(loged in) client
    @DeleteMapping("/client/address")
    public HashMap<String, Object> deleteAddress(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // validate address purpose
        if (!params.containsKey("address_purpose")) {
            response.put("message", "Field address_purpose is required.");
            return response;
        }
        String addressPurposeString = params.get("address_purpose").toUpperCase();
        if (addressPurposeString.isBlank()) {
            response.put("message", "Field address_purpose can't be blank.");
            return response;
        }
        AddressPurpose addressPurpose = null;
        for (AddressPurpose ap : AddressPurpose.values()) {
            if (addressPurposeString.equals(ap.toString())) {
                addressPurpose = ap;
            }
        }
        if (addressPurpose == null) {
            response.put("message", "Field address_purpose must be a valid address purpose(CHARGE, DELIVER).");
            return response;
        }

        // all validation tests passed

        // delete address
        addressService.deleteAddress(client, addressPurpose);

        //return response
        response.put("error", false);
        return response;
    }

}
