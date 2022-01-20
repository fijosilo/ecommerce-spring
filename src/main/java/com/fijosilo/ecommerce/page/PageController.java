package com.fijosilo.ecommerce.page;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(value = "/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readPage(@PathVariable("title") String title) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate title
        if (title.isBlank()) {
            payload.put("error", "This page was not found.");
            return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
        }
        // validate page
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            payload.put("error", "This page was not found.");
            return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
        }

        // return content
        payload.put("page", page.getContent());
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> createPage(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate title
        if (!params.containsKey("title")) {
            payload.put("error", "Field title is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            payload.put("error", "Field title can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page doesn't exist
        Page page = pageService.readPageByTitle(title);
        if (page != null) {
            payload.put("error", "Field title can't be the title of an already existing page.");
            return new ResponseEntity<>(payload, HttpStatus.CONFLICT);
        }

        // validate content
        if (!params.containsKey("content")) {
            payload.put("error", "Field content is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String content = params.get("content");
        if (content.isBlank()) {
            payload.put("error", "Field content can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // create and save the info
        page = new Page();
        page.setTitle(title);
        page.setContent(content);
        if (!pageService.createPage(page)) {
            payload.put("error", "Database couldn't register the page.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // info got registered
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> updatePage(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate title
        if (!params.containsKey("title")) {
            payload.put("error", "Field title is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            payload.put("error", "Field title can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page does exist
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            payload.put("error", "Field title must be a valid page title.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate content
        String content = null;
        if (params.containsKey("content")) {
            content = params.get("content");
            if (content.isBlank()) {
                payload.put("error", "Field content can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // update the page
        if (content != null) page.setContent(content);
        if (!pageService.updatePage(page)) {
            payload.put("error", "Database couldn't update the page.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // page got updated
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @DeleteMapping(value = "/admin/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> deletePage(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate title
        if (!params.containsKey("title")) {
            payload.put("error", "Field title is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            payload.put("error", "Field title can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page does exist
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            payload.put("error", "Field title must be a valid page title.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // delete the page
        if (!pageService.deletePage(page)) {
            payload.put("error", "Database couldn't delete the page.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // page got deleted
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
