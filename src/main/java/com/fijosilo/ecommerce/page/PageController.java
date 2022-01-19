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

    @GetMapping(value = "/{title}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> readPage(@PathVariable("title") String title) {
        // validate title
        if (title.isBlank()) {
            return new ResponseEntity<>("This page was not found.", HttpStatus.NOT_FOUND);
        }
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            return new ResponseEntity<>("This page was not found.", HttpStatus.NOT_FOUND);
        }

        // return content
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @PostMapping(value = "/admin/page", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createPage(@RequestParam HashMap<String, String> params) {
        // validate title
        if (!params.containsKey("title")) {
            return new ResponseEntity<>("Field title is required.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            return new ResponseEntity<>("Field title can't be blank.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page doesn't exist
        Page page = pageService.readPageByTitle(title);
        if (page != null) {
            return new ResponseEntity<>("Field title can't be the title of an already existing page.", HttpStatus.CONFLICT);
        }

        // validate content
        if (!params.containsKey("content")) {
            return new ResponseEntity<>("Field content is required.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String content = params.get("content");
        if (content.isBlank()) {
            return new ResponseEntity<>("Field content can't be blank.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // create and save the info
        page = new Page();
        page.setTitle(title);
        page.setContent(content);
        if (!pageService.createPage(page)) {
            return new ResponseEntity<>("Database couldn't register the page.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // info got registered
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/page", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updatePage(@RequestParam HashMap<String, String> params) {
        // validate title
        if (!params.containsKey("title")) {
            return new ResponseEntity<>("Field title is required.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            return new ResponseEntity<>("Field title can't be blank.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page does exist
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            return new ResponseEntity<>("Field title must be a valid page title.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate content
        String content = null;
        if (params.containsKey("content")) {
            content = params.get("content");
            if (content.isBlank()) {
                return new ResponseEntity<>("Field content can't be blank.", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // update the page
        if (content != null) page.setContent(content);
        if (!pageService.updatePage(page)) {
            return new ResponseEntity<>("Database couldn't update the page.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // page got updated
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping(value = "/admin/page", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deletePage(@RequestParam HashMap<String, String> params) {
        // validate title
        if (!params.containsKey("title")) {
            return new ResponseEntity<>("Field title is required.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String title = params.get("title");
        if (title.isBlank()) {
            return new ResponseEntity<>("Field title can't be blank.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the page does exist
        Page page = pageService.readPageByTitle(title);
        if (page == null) {
            return new ResponseEntity<>("Field title must be a valid page title.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // delete the page
        if (!pageService.deletePage(page)) {
            return new ResponseEntity<>("Database couldn't delete the page.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // page got deleted
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
