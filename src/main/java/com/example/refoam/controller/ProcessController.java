package com.example.refoam.controller;

import com.example.refoam.domain.Process;
import com.example.refoam.service.OrderService;
import com.example.refoam.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final OrderService orderService;

    @GetMapping("/{id}/list")
    public String processList(@PathVariable("id") Long orderId, Model model){
        List<Process> processes = processService.findAllOrder(orderId);
        model.addAttribute("processes",processes);
        model.addAttribute("orderId",orderId);

        return "process/processList";
    }
}
