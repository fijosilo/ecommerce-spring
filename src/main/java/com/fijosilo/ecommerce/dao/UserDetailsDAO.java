package com.fijosilo.ecommerce.dao;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsDAO {
    UserDetails getUserDetailsByEmail(String email);
}
