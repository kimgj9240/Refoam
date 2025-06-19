package com.example.refoam.service;

import com.example.refoam.DiscordProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotifier {
    // 디스코드 알림용 클래스
    private final DiscordProperties discordProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendAlert(String content) {
        String url = discordProperties.getUrl();
        log.info("디스코드 전송 URL 확인: {}", url);
        log.info("디스코드 메시지: {}", content);

        if (url == null || url.isBlank()) {
            log.error("Webhook URL이 null이거나 비어 있음");
            return;
        }

        Map<String, String> payload = Map.of("content", content);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("디스코드 응답: {}, 본문: {}", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("디스코드 알림 전송 실패", e);
        }
    }
}