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
@RequestMapping("/hello")
public class HelloController {

    /**
     * GET
     * POST
     * PUT
     * DELETE
     */

    // http://127.0.0.1:8080/hello/world

    @GetMapping("world")
    public String world() {
        return "Hello duqy!";
    }

}
