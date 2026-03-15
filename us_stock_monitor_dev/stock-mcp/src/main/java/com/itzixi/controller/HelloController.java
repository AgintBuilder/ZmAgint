package com.itzixi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
@ClassName HelloController
@Author duqy
@Version 1.0
@Description HelloController
**/
@RestController
@RequestMapping("/")
public class HelloController {

    /**
     * GET
     * POST
     * PUT
     * DELETE
     */
    @GetMapping("/")
    public String world() {
        return "Hello mcp-service!";
    }

}
