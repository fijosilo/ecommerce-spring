package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.repository.JPAClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    private String resourceFolder;

    private static final Logger log = LoggerFactory.getLogger(JPAClientRepository.class);

    public ImageController(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    @GetMapping("/**")
    public HashMap<String, Object> download() {
        //
        HashMap<String, Object> response = new HashMap<>();
        //
        response.put("test", "download image");
        //
        return response;
    }

    @PostMapping
    public HashMap<String, Object> upload(@RequestParam("image") MultipartFile file) {
        HashMap<String, Object> response = new HashMap<>();

        // give the image a unique name
        String name = UUID.randomUUID().toString();

        // get the image extension
        String extension = file.getContentType().replaceFirst(".*\\/", "");

        // TODO: resize the image if the size is bigger than YxZ

        // save the image
        String relPath = String.format("/image/%s.%s", name, extension);
        String absPath = String.format("%s/%s", resourceFolder, relPath);
        try {
            FileOutputStream out = new FileOutputStream(absPath);
            out.write(file.getBytes());
            out.close();

            response.put("error", false);
            response.put("url", relPath);
            return response;
        } catch (IllegalArgumentException | SecurityException | IOException e) {
            log.warn(e.getMessage());

            response.put("error", true);
            response.put("message", "Server couldn't save the image.");
            return response;
        }
    }

}
