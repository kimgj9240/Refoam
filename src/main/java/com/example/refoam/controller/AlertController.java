package com.example.refoam.controller;

import com.example.refoam.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/alert")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping("/read/{id}")
    public String readAndRedirect(@PathVariable Long id, @RequestParam("orderId") Long orderId) {
        alertService.markAsRead(id);
        return String.format("redirect:/process/%d/list", orderId);
    }

}
