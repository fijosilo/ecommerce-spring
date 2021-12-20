package com.fijosilo.ecommerce.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/admin/product")
public class ManageProductController {

    @PostMapping
    public HashMap<String, Object> add(@RequestParam HashMap<String, Object> params) {
        //
        HashMap<String, Object> response = new HashMap<>();
        //
        response.put("test", "product add");
        //
        return response;
    }

    @PutMapping
    public HashMap<String, Object> update(@RequestParam HashMap<String, Object> params) {
        //
        HashMap<String, Object> response = new HashMap<>();
        //
        response.put("test", "product update");
        //
        return response;
    }

    @DeleteMapping
    public HashMap<String, Object> delete(@RequestParam HashMap<String, Object> params) {
        //
        HashMap<String, Object> response = new HashMap<>();
        //
        response.put("test", "product delete");
        //
        return response;
    }

}
