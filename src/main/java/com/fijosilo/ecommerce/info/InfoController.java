package com.fijosilo.ecommerce.info;

import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class InfoController {
    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/info/{title}")
    public ResponseEntity<String> readInfo(@PathVariable("title") String title) {
        // validate title
        if (title.isBlank()) {
            return new ResponseEntity<>("Field title can't be blank.", HttpStatus.NOT_FOUND);
        }
        Info info = infoService.readInfoByTitle(title);
        if (info == null) {
            return new ResponseEntity<>("Field title must be a valid info title.", HttpStatus.NOT_FOUND);
        }

        // return content
        return new ResponseEntity<>(info.getContent(), HttpStatus.OK);
    }

    @PostMapping("/admin/info")
    public HashMap<String, Object> createInfo(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate title
        if (!params.containsKey("title")) {
            response.put("message", "Field title is required.");
            return response;
        }
        String title = params.get("title");
        if (title.isBlank()) {
            response.put("message", "Field title can't be blank.");
            return response;
        }
        Info info = infoService.readInfoByTitle(title);
        if (info != null) {
            response.put("message", "A info with the provided title already exists in the database.");
            return response;
        }

        // validate content
        if (!params.containsKey("content")) {
            response.put("message", "Field content is required.");
            return response;
        }
        String content = params.get("content");
        if (content.isBlank()) {
            response.put("message", "Field content can't be blank.");
            return response;
        }

        // all validations test passed

        // create and save the info
        info = new Info();
        info.setTitle(title);
        info.setContent(content);
        if (!infoService.createInfo(info)) {
            response.put("message", "Database couldn't register the info.");
            return response;
        }

        // info got registered
        response.put("error", false);
        return response;
    }

    @PutMapping("/admin/info")
    public HashMap<String, Object> updateInfo(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate info
        if (!params.containsKey("title")) {
            response.put("message", "Field title is required.");
            return response;
        }
        String title = params.get("title");
        if (title.isBlank()) {
            response.put("message", "Field title can't be blank.");
            return response;
        }
        Info info = infoService.readInfoByTitle(title);
        if (info == null) {
            response.put("message", "Field title must be a valid info title.");
            return response;
        }

        // optional validate content
        String content = null;
        if (params.containsKey("content")) {
            content = params.get("content");
            if (content.isBlank()) {
                response.put("message", "Field content can't be blank.");
                return response;
            }
        }

        // all validations test passed

        // update the info
        if (content != null) info.setContent(content);
        if (!infoService.updateInfo(info)) {
            response.put("message", "Database couldn't update the info.");
            return response;
        }

        // category got updated
        response.put("error", false);
        return response;
    }

    @DeleteMapping("/admin/info")
    public HashMap<String, Object> deleteInfo(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate info
        if (!params.containsKey("title")) {
            response.put("message", "Field title is required.");
            return response;
        }
        String title = params.get("title");
        if (title.isBlank()) {
            response.put("message", "Field title can't be blank.");
            return response;
        }
        Info info = infoService.readInfoByTitle(title);
        if (info == null) {
            response.put("message", "Field title must be a valid info title.");
            return response;
        }

        // all validations test passed

        // delete the category
        if (!infoService.deleteInfo(info)) {
            response.put("message", "Database couldn't delete the info.");
            return response;
        }

        // category got deleted
        response.put("error", false);
        return response;
    }

}
