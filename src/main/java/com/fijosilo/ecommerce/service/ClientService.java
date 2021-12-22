package com.fijosilo.ecommerce.service;

import com.fijosilo.ecommerce.dao.ClientDAO;
import com.fijosilo.ecommerce.dto.Client;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService(@Qualifier("JPAClientRepository") ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public boolean createClient(Client client) {
        return clientDAO.createClient(client);
    }

    public Client readClientByEmail(String email) {
        return clientDAO.readClientByEmail(email);
    }

    public boolean updateClient(Client client) {
        return clientDAO.updateClient(client);
    }

    public boolean deleteClient(Client client) {
        return clientDAO.deleteClient(client);
    }
}
