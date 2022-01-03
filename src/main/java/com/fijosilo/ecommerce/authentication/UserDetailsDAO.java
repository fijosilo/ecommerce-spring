package com.fijosilo.ecommerce.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsDAO {
    UserDetails getUserDetailsByEmail(String email);
}
