package com.example.refoam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @GetMapping("/new")
    public String create(){
        return "employee/createEmployeeForm";
    }
    @GetMapping("/list")
    public String list(){
        return "employee/employeeList";
    }
    @GetMapping("/edit")
    public String update(){
        return "employee/editEmployeeForm";
    }
}
