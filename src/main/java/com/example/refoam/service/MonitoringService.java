package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductLabel;
import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final OrderRepository orderRepository;
    private final ProcessRepository processRepository;
    private final OpenAiService openAiService;

    public List<ProductionMonitoring> productionMonitorings() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        // 전체 공정 조회
        List<Process> processes = processRepository.findProcessesInDateRange(start, end);
        List<Orders> orders = orderRepository.findAll();

        return processes.stream()
                .collect(Collectors.groupingBy(p -> p.getProcessDate().toLocalDate()))
                .entrySet()
                .stream()
                .map(entry -> {

                    // 날짜별
                    LocalDate date = entry.getKey();
                    List<Process> processList = entry.getValue();

                    // ERR로 시작하는 라벨 집계
                    int errCount = (int) processList.stream()
                            .filter(p -> p.getStatus().startsWith("ERR"))
                            .count();
                    // OK로 시작하는 라벨 집계
                    int okCount = (int) processList.stream()
                            .filter(p -> "OK".equals(p.getStatus()))
                            .count();
                    // orderId 기준으로 중복 제거한 주문 건수 집계
                    int orderCount = orders.stream()
                            .filter(o -> o.getOrderDate().toLocalDate().equals(date))
                            .mapToInt(Orders::getOrderQuantity)
                            .sum();
                    // 온도 에러 집계
                    int errTempCount = (int) processList.stream()
                            .filter(p -> p.getStandard().getProductLabel() == ProductLabel.ERR_TEMP)
                            .count();
                    // 시간 에러 집계
                    int errTimeCount = (int) processList.stream()
                            .filter(p -> p.getStandard().getProductLabel() == ProductLabel.ERR_TIME)
                            .count();

                    // 배합 실패 집계
                    int mixFailCount = orders.stream()
                            .filter(o -> o.getOrderDate().toLocalDate().equals(date))
                            .filter(o -> "배합실패".equals(o.getOrderState()))
                            .mapToInt(Orders::getOrderQuantity)
                            .sum();

                    // DTO 반환
                    return ProductionMonitoring.builder()
                            .date(date)
                            .errCount(errCount)
                            .okCount(okCount)
                            .orderCount(orderCount)
                            .errTempCount(errTempCount)
                            .errTimeCount(errTimeCount)
                            .mixFailCount(mixFailCount)
                            .build();
                })
                // 날짜 오름차순 정렬
                .sorted(Comparator.comparing(ProductionMonitoring::getDate))
                .collect(Collectors.toList());
    }

    public Map<String, Long> errorCounts() {
        // 당일 공정 리스트
        List<Process> processes = processRepository.findTodayProcesses();
        List<Orders> orders = orderRepository.findMixFail();

        long errTempCount = processes.stream()
                .filter(p -> p.getStandard().getProductLabel() == ProductLabel.ERR_TEMP)
                .count();

        long errTempTimeCount = processes.stream()
                .filter(p -> p.getStandard().getProductLabel() == ProductLabel.ERR_TIME)
                .count();

        long mixFailCount = orders.size();

        Map<String, Long> result = new LinkedHashMap<>();
        result.put("배합실패", mixFailCount);
        result.put("ERR_TEMP", errTempCount);
        result.put("ERR_TIME", errTempTimeCount);

        return result;
    }


    public Map<String, Integer> targetAchievement(int minTarget, int maxTarget, int targetRate){
        Random random = new Random(LocalDate.now().toEpochDay());

        int targetQuantity = random.nextInt(maxTarget - minTarget + 1) + minTarget;

        int targetAchieveQuantity = (int) (targetQuantity * (targetRate / 100.0));

        List<Process> todayProcess = processRepository.findTodayProcesses();
        int okCount = (int) todayProcess.stream()
                .filter(p -> "OK".equals(p.getStatus()))
                .count();

        int achievementRate = targetQuantity > 0 ? (int) ((double) okCount / targetQuantity * 100) : 0;

        Map<String, Integer> result = new HashMap<>();
        result.put("targetQuantity", targetQuantity);
        result.put("targetAchieveQuantity", targetAchieveQuantity);
        result.put("okCount",okCount);
        result.put("achievementRate", achievementRate);
        return result;
    }
    public String generateErrorReport() {
        Map<String, Long> errorCounts = errorCounts();

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
        """, errTemp, errTime, mixFail);
        return openAiService.generateReport(prompt);
    }

}


