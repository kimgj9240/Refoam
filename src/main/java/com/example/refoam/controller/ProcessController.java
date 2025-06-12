package com.example.refoam.controller;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.repository.ProcessRepository;
import com.example.refoam.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final OrderService orderService;
    private final QualityCheckService qualityCheckService;

    @GetMapping("/{id}/list")
    public String processList(@PathVariable("id") Long orderId, Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Process> paging = processService.getList(orderId, page);
        model.addAttribute("misMatchCount",qualityCheckService.getMismatchCount(orderId));
        model.addAttribute("processes",paging);
        model.addAttribute("qualityCheck",qualityCheckService.selectQualityCheck(orderId));
        model.addAttribute("orderId",orderId);
        model.addAttribute("activeMenu", 3);
        return "process/processList";
    }

    @PostMapping("/{id}/list")
    public String startProcess(@PathVariable("id") Long orderId, @RequestParam(value = "page", defaultValue = "0") int page, RedirectAttributes redirectAttributes) {
        Orders order = orderService.findOneOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        // 1차 상태 검증
        if (order.getOrderState().equals("진행 중")) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 진행된 공정입니다.");
            return "redirect:/order/list?page=" + page;
        }
        processService.startMainProcess(orderId);
        return "redirect:/order/list?page=" + page;
    }
}
