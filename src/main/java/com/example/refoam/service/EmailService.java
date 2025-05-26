package com.example.refoam.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendErrorAlert(String to, int orderQuantity, int errorCount) {
        double errorRate = (errorCount * 100.0) / orderQuantity;

        String subject = "제품 생산 알람: 에러율 초과";
        String body = String.format(
                "에러가 감지되었습니다!\n\n주문 수량: %d\n에러 수량: %d\n에러율: %.2f%%",
                orderQuantity, errorCount, errorRate
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
