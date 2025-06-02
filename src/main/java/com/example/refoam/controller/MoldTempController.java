package com.example.refoam.controller;

import com.example.refoam.dto.MoldTempForm;
import com.example.refoam.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/temperature")
public class MoldTempController {
    private final ProcessService processService;

    @GetMapping("/{productName}")
    public List<MoldTempForm> getTemperatureHistory(@PathVariable String productName) {
        return processService.findRecentMoldTemperatures(productName.toUpperCase());
    }
}
