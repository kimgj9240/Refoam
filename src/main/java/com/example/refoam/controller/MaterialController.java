package com.example.refoam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
@Controller
@RequestMapping("/material")
public class MaterialController {


    @GetMapping("/new")
    public String create(){
        return "material/createMaterialForm";
    }
    @GetMapping("/list")
    public String list(){
        return "material/materialList";
    }
    @GetMapping("/edit")
    public String update(){
        return "material/editMaterialForm";
    }

}
