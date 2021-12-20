package com.fijosilo.ecommerce.dao;

import com.fijosilo.ecommerce.dto.User;

public interface UserDAO {
    boolean createUser(User user);
    User readUserByEmail(String email);
    boolean updateUser(User user);
    boolean deleteUser(User user);
}
