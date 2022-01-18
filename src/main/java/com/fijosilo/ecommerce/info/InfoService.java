package com.fijosilo.ecommerce.info;

import org.springframework.stereotype.Service;

@Service
public class InfoService {
    private final InfoDAO infoDAO;

    public InfoService(InfoDAO infoDAO) {
        this.infoDAO = infoDAO;
    }

    public boolean createInfo(Info info) {
        return infoDAO.createInfo(info);
    }

    public Info readInfoByTitle(String title) {
        return infoDAO.readInfoByTitle(title);
    }

    public boolean updateInfo(Info info) {
        return infoDAO.updateInfo(info);
    }

    public boolean deleteInfo(Info info) {
        return infoDAO.deleteInfo(info);
    }

}
