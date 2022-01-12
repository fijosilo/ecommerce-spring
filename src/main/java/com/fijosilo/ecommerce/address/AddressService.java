package com.fijosilo.ecommerce.address;

import com.fijosilo.ecommerce.authentication.Client;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressDAO addressDAO;

    public AddressService(@Qualifier("JPAAddressRepository") AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public boolean createAddress(Address address) {
        return addressDAO.createAddress(address);
    }

    public boolean updateAddress(Address address) {
        return addressDAO.updateAddress(address);
    }

    public boolean deleteAddress(Client client, AddressPurpose addressPurpose) {
        return addressDAO.deleteAddress(client, addressPurpose);
    }

}
