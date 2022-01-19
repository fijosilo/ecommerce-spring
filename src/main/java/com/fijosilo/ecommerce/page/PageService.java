package com.fijosilo.ecommerce.page;

import org.springframework.stereotype.Service;

@Service
public class PageService {
    private final PageDAO pageDAO;

    public PageService(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }

    public boolean createPage(Page page) {
        return pageDAO.createPage(page);
    }

    public Page readPageByTitle(String title) {
        return pageDAO.readPageByTitle(title);
    }

    public boolean updatePage(Page page) {
        return pageDAO.updatePage(page);
    }

    public boolean deletePage(Page page) {
        return pageDAO.deletePage(page);
    }

}
