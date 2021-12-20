package com.fijosilo.ecommerce.repository;

import com.fijosilo.ecommerce.configuration.ImplementationUserDetails;
import com.fijosilo.ecommerce.dao.UserDetailsDAO;
import com.fijosilo.ecommerce.dto.User;
import com.fijosilo.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository("JPAUserDetailsRepository")
public class JPAUserDetailsRepository implements UserDetailsDAO {
    private final UserService userService;

    public JPAUserDetailsRepository(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails getUserDetailsByEmail(String email) {
        User user = userService.readUserByEmail(email);
        ImplementationUserDetails userDetails = (user != null) ? new ImplementationUserDetails(user) : null;
        return userDetails;
    }

}
