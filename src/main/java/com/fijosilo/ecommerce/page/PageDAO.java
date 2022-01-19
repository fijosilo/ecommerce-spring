package com.fijosilo.ecommerce.page;

public interface PageDAO {
    boolean createPage(Page page);
    Page readPageByTitle(String title);
    boolean updatePage(Page page);
    boolean deletePage(Page page);
}
