package com.ohmyfun.csquiz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class AdminController {
    @GetMapping("/admin")
    public String adminP() {

        return "admin Controller";
    }
}
