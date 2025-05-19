package com.example.refoam.controller;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Material;
import com.example.refoam.service.MaterialService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialController {
    private final MaterialService materialService;
    @GetMapping("/new")
    public String create(){return "material/createMaterialForm";}
    @GetMapping("/list")
    public String list(Model model){
        List<Material> materialList = materialService.selectAll();
        model.addAttribute("materialList",materialList);
        return "material/materialList";
    }
    @GetMapping("/edit")
    public String update(){
        return "material/editMaterialForm";
    }
}
