package com.example.refoam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/monitoring")
public class MonitoringController {
    @GetMapping("/main")
    public String create(){
        return "monitoring/errorMonitoring";
    }
}
