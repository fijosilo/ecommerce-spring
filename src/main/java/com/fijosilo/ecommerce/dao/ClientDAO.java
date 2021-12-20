package com.fijosilo.ecommerce.dao;

import com.fijosilo.ecommerce.dto.Client;

public interface ClientDAO {
    boolean createClient(Client client);
    Client readClientByEmail(String email);
    boolean updateClient(Client client);
    boolean deleteClient(Client client);
}
