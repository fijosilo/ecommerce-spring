package com.fijosilo.ecommerce.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderConfig {

    @Bean
    public String resourceFolder() {
        return System.getenv("APPDATA") + "/SpringBoot/eCommerceApp";
    }

}
