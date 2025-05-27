package com.example.refoam.controller;

import com.example.refoam.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alert")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAlert() {
        boolean alert = alertService.isAnyOrderExceeded();
        Map<String, Object> result = new HashMap<>();
        result.put("alert", alert);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/read/{id}")
    public String readAndRedirect(@PathVariable("id") Long alertId, @RequestParam("orderId") Long orderId) {
        alertService.markAsRead(alertId);
        return String.format("redirect:/process/%d/list", orderId);
    }

}
