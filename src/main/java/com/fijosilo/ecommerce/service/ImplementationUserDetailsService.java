package com.fijosilo.ecommerce.service;

import com.fijosilo.ecommerce.dao.UserDetailsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplementationUserDetailsService implements UserDetailsService {
    private final UserDetailsDAO userDetailsDAO;

    public ImplementationUserDetailsService(@Qualifier("JPAUserDetailsRepository") UserDetailsDAO userDetailsDAO) {
        this.userDetailsDAO = userDetailsDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username is actually modified in the SecurityConfig.java to be the user email
        UserDetails userDetails = userDetailsDAO.getUserDetailsByEmail(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("No user with the provided email was found");
        }
        return userDetails;
    }

}
