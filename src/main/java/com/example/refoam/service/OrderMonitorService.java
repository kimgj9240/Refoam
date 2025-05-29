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

        log.info("에러 체크 {}", email);
        emailService.sendErrorAlert(email,orderQuantity,errorCount);
        //if (errorRate >= 0.4) {
            //emailService.sendErrorAlert("refoam.test@mail.com",orderQuantity,errorCount);

        //}
    }
}
