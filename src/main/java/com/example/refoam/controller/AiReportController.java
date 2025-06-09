package com.example.refoam.controller;

import com.example.refoam.service.MonitoringService;
import com.example.refoam.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AiReportController {

    private final MonitoringService monitoringService;
    private final OpenAiService openAiService;

    @GetMapping("/ai-summary")
    public String aiSummary() {
        Map<String, Integer> kpiMap = monitoringService.targetAchievement(100, 400);
        Map<String, Long> errorCounts = monitoringService.errorCounts();

        int target = kpiMap.get("targetQuantity");
        int ok = kpiMap.get("okCount");
        int rate = kpiMap.get("achievementRate");

        long errTemp = errorCounts.getOrDefault("ERR_TEMP", 0L);
        long errTime = errorCounts.getOrDefault("ERR_TIME", 0L);
        long mixFail = errorCounts.getOrDefault("배합실패", 0L);


        String prompt = String.format(
                """
                오늘의 생산 리포트를 작성해줘. 다음 정보를 참고해서 다음 4가지 항목을 포함해줘:
                1. 전반적인 생산 요약
                2. 불량 유형별 통계와 원인 분석
                3. 성과 분석 (목표 대비 달성률)
                4. 내일을 위한 개선 방향 또는 경고
            
                📊 생산 성과:
                - 목표 수량: %d개
                - OK 수량: %d개
                - 달성률: %d%%
            
                ⚠️ 에러 통계:
                - 온도 에러: %d건
                - 시간 에러: %d건
                - 배합 실패: %d건
            
                관리자에게 보고하는 형식으로 작성해줘. 포맷은 깔끔하고 핵심 위주로, 너무 길지 않게.
                """, target, ok, rate, errTemp, errTime, mixFail
        );
        return openAiService.generateReport(prompt);
    }
}
