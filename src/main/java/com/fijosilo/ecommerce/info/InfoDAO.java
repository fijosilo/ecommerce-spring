package com.fijosilo.ecommerce.info;

public interface InfoDAO {
    boolean createInfo(Info info);
    Info readInfoByTitle(String title);
    boolean updateInfo(Info info);
    boolean deleteInfo(Info info);
}
