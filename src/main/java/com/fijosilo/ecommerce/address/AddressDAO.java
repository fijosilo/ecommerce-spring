package com.fijosilo.ecommerce.address;

import com.fijosilo.ecommerce.authentication.Client;

public interface AddressDAO {
    Address readAddressById(Long id);
    boolean createAddress(Address address);
    boolean updateAddress(Address address);
    boolean deleteAddress(Client client, AddressPurpose addressPurpose);
}
