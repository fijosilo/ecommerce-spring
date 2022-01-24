package com.fijosilo.ecommerce.address;

public interface AddressDAO {
    Address readAddressById(Long id);
    boolean createAddress(Address address);
    boolean updateAddress(Address address);
    boolean deleteAddress(Address address);
}
