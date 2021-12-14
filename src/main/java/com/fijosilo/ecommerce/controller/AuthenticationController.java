package com.fijosilo.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/")
    public String test() {
        //
        System.out.println("TEST");
        return "test endpoint";
    }

    @PostMapping("/register")
    public String register() {
        //
        System.out.println("REGISTER");
        return "register endpoint";
    }

}
