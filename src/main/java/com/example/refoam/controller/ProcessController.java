package com.example.refoam.controller;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final QualityCheckService qualityCheckService;

    @GetMapping("/{id}/list")
    public String processList(@PathVariable("id") Long orderId, Model model){
        List<Process> processes = processService.findAllOrder(orderId);
        int qualityCheckCount = qualityCheckService.selectQualityCheck(orderId);
        model.addAttribute("processes",processes);
        model.addAttribute("qualityCheckCount",qualityCheckCount);
        model.addAttribute("orderId",orderId);
        return "process/processList";
    }
    @PostMapping("/{id}/list")
    public String startProcess(@PathVariable("id") Long orderId) {
        processService.startMainProcess(orderId);
        return "redirect:/order/list";
    }
}
