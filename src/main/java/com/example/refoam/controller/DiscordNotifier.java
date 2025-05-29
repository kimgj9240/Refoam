package com.example.refoam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordNotifier {
    // 디스코드 알림용 클래스
    private String webhookUrl = "https://discord.com/api/webhooks/1377189847684481034/PNy7HmUgr900hSQI6_pz-XA-lRqCFqlnxJ71EorTDIvjKw5-Ffh4dIGVZR0J-W58IjzW";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendAlert(String content) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("content", content); // 디스코드에 표시될 메시지

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch (Exception e) {
            log.error("디스코드 알림 전송 실패", e);
        }
    }
}