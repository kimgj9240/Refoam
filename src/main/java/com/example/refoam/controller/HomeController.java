package com.example.refoam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "home";
    }
    @GetMapping("/table")
    public String table(){
        return "table";
    }
    @GetMapping("/form")
    public String form(){
        return "form";
    }
}
