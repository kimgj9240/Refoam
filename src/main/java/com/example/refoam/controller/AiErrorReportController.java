package com.example.refoam.controller;


import com.example.refoam.service.MonitoringService;
import com.example.refoam.service.OpenAiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AiErrorReportController {

    private final MonitoringService monitoringService;
    private final OpenAiService openAiService;

    @GetMapping("/ai-error-summary")
    public String aiErrorSummary() {
        Map<String, Long> errorCounts = monitoringService.errorCounts();

        long errTemp = errorCounts.getOrDefault("ERR_TEMP", 0L);
        long errTime = errorCounts.getOrDefault("ERR_TIME", 0L);
        long mixFail = errorCounts.getOrDefault("배합실패", 0L);

        String prompt = String.format("""
        오늘의 불량 통계를 기반으로 에러 모니터링 리포트를 작성해줘.
        아래 정보를 참고해서 다음 항목을 포함해줘:
        1. 에러 유형별 발생량 요약
        2. 문제 원인 분석 (가능하다면)
        3. 향후 주의 사항 또는 개선 제안

        📉 에러 통계:
        - 온도 에러(ERR_TEMP): %d건
        - 시간 에러(ERR_TIME): %d건
        - 배합 실패: %d건

        관리자 보고용으로 간결하고 핵심만 정리해줘.
        """, errTemp, errTime, mixFail
        );return openAiService.generateReport(prompt);
    }
}
