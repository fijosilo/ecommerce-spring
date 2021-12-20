package com.fijosilo.ecommerce.repository;

import com.fijosilo.ecommerce.configuration.ImplementationUserDetails;
import com.fijosilo.ecommerce.dao.UserDetailsDAO;
import com.fijosilo.ecommerce.dto.Client;
import com.fijosilo.ecommerce.service.ClientService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository("JPAUserDetailsRepository")
public class JPAUserDetailsRepository implements UserDetailsDAO {
    private final ClientService clientService;

    public JPAUserDetailsRepository(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public UserDetails getUserDetailsByEmail(String email) {
        Client client = clientService.readClientByEmail(email);
        ImplementationUserDetails userDetails = (client != null) ? new ImplementationUserDetails(client) : null;
        return userDetails;
    }

}
