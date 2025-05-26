package com.example.refoam.controller;

import com.example.refoam.service.QualityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/quality")
@RequiredArgsConstructor
public class QualityCheckController {
    private final QualityCheckService qualityCheckService;
    @GetMapping("/{id}/check")
    public String check(@PathVariable("id") Long orderId){
        qualityCheckService.getQualityCheck(orderId);

        return "redirect:/process/{id}/list";
    }
}
