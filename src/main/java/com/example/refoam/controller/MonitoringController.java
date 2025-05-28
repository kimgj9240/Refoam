package com.example.refoam.controller;

import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/main")
    public String create(Model model){
        model.addAttribute("activeMenu", 4);

        List<ProductionMonitoring> productionMonitorings = monitoringService.productionMonitorings();
        model.addAttribute("productionMonitorings", productionMonitorings);

        Map<String, Long> TodayErrorCounts = monitoringService.errorCounts();
        model.addAttribute("TodayErrorCounts", TodayErrorCounts);

        Map<String, Long> errorData = monitoringService.errorCounts();
        String errorSummary = monitoringService.generateErrorReport();

        model.addAttribute("errorCounts", errorData);
        model.addAttribute("errorSummary", errorSummary);

        return "monitoring/errorMonitoring";
    }
}
