package com.fijosilo.ecommerce.authentication;

public interface ClientDAO {
    boolean createClient(Client client);
    Client readClientByEmail(String email);
    boolean updateClient(Client client);
    boolean deleteClient(Client client);
}
