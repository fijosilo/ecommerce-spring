package com.fijosilo.ecommerce.service;

import com.fijosilo.ecommerce.dao.UserDAO;
import com.fijosilo.ecommerce.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDAO userDAO;

    public UserService(@Qualifier("JPAUserRepository") UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean createUser(User user) {
        return userDAO.createUser(user);
    }

    public User readUserByEmail(String email) {
        return userDAO.readUserByEmail(email);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(User user) {
        return userDAO.deleteUser(user);
    }
}
