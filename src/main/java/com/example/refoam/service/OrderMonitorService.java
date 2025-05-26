package com.example.refoam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderMonitorService {
    private final EmailService emailService;
    @Transactional
    public void errorCheck(String email,int orderQuantity, int errorCount){
        if(orderQuantity == 0) return;

        double errorRate = (double) errorCount / orderQuantity;

        if (errorRate >= 0.4) {
            //emailService.sendErrorAlert("refoam.test@mail.com",orderQuantity,errorCount);
            emailService.sendErrorAlert(email,orderQuantity,errorCount);
        }
    }
}
