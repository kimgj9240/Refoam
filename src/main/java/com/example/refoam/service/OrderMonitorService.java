package com.example.refoam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMonitorService {
    private final EmailService emailService;
    @Transactional
    public void errorCheck(String email,int orderQuantity, int errorCount){
        if(orderQuantity == 0) return;
        emailService.sendErrorAlert(email,orderQuantity,errorCount);
    }
}
