package com.example.refoam.controller;


import com.example.refoam.dto.ProductChartDataForm;
import com.example.refoam.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/production")
public class ProductChartController {
    private final ProcessService processService;

    @GetMapping("/by-product")
    public List<ProductChartDataForm> getProductionStatsByProduct() {
        return processService.getStatsByProduct(); // 생산량, 불량률 통계
    }
}