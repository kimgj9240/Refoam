package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductLabel;
import com.example.refoam.domain.ProductName;
import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

    public Map<ProductName, List<ProductionMonitoring>> allProductionMonitoringsWithPadding() {
        // 1. 기준 날짜 리스트 생성 (ALL)
        List<ProductionMonitoring> allMonitorings = productionMonitorings("ALL");
        List<LocalDate> allDates = allMonitorings.stream()
                .map(ProductionMonitoring::getDate)
                .distinct()
                .sorted()
                .toList();

        // 2. 각 제품별 모니터링 결과 수집
        Map<ProductName, List<ProductionMonitoring>> result = new HashMap<>();
        for (ProductName product : List.of(ProductName.NORMAL, ProductName.BUMP, ProductName.HALF)) {
            List<ProductionMonitoring> rawMonitorings = productionMonitorings(product.name());

            // 날짜 -> 데이터 매핑
            Map<LocalDate, ProductionMonitoring> dateMap = rawMonitorings.stream()
                    .collect(Collectors.toMap(ProductionMonitoring::getDate, pm -> pm));

            // 누락된 날짜를 0으로 채워 넣기
            List<ProductionMonitoring> padded = allDates.stream()
                    .map(date -> dateMap.getOrDefault(date,
                            ProductionMonitoring.builder()
                                    .product(product.name())
                                    .date(date)
                                    .errCount(0).okCount(0).orderCount(0)
                                    .errTempCount(0).errTimeCount(0).mixFailCount(0)
                                    .build()))
                    .toList();

            result.put(product, padded);
        }
        result.put(null, allMonitorings);

        return result;
    }

    public List<ProductionMonitoring> productionMonitorings(String productName) {//제품별 카운팅하는거땜에 매게변수 추가
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        List<Process> processes = new ArrayList<>();
        // 전체 공정 조회
        if(productName.equals("ALL")){
            processes = processRepository.findProcessesInDateRange(start, end);
        }else {
            processes = processRepository.findProcessesInDateRange(start, end, ProductName.valueOf(productName.toUpperCase()));
        }


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
                    int okCount = (int) Optional.ofNullable(processList)
                            .orElse(Collections.emptyList())
                            .stream()
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
                            .product(productName)
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




    public Map<String, Integer> targetAchievement(int minTarget, int maxTarget) {

        List<Process> todayProcess = processRepository.findTodayProcesses();

        if (todayProcess.isEmpty()) {
            System.out.println("공정 미시작 상태: 달성률 전송 생략");
            return Map.of(
                    "targetQuantity", 0,
                    "okCount", 0,
                    "achievementRate", 0
            );
        }


        int targetQuantity = getTodayTarget(minTarget, maxTarget);

        int okCount = (int) todayProcess.stream()
                .filter(p -> "OK".equals(p.getStatus()))
                .count();

        int achievementRate = targetQuantity > 0 ? (int) ((double) okCount / targetQuantity * 100) : 0;

        return Map.of(
                "targetQuantity", targetQuantity,
                "okCount", okCount,
                "achievementRate", achievementRate
        );
    }

    public void sendAchievementUpdate() {
        List<Process> todayProcess = processRepository.findTodayProcesses();

//        if (todayProcess.isEmpty()) {
//            System.out.println("공정 없음 - 전송 생략");
//            return;
//        }

        int targetQuantity = getTodayTarget(150,250);

        int okCount = (int) todayProcess.stream()
                .filter(p -> "OK".equals(p.getStatus()))
                .count();

        int achievementRate = targetQuantity > 0 ? (int) ((double) okCount / targetQuantity * 100) : 0;

        Map<String, Object> socket = new HashMap<>();
        socket.put("targetQuantity", targetQuantity); // 목표 수량
        socket.put("okCount", okCount); // 완제품
        socket.put("achievementRate", achievementRate); // 달성률
        simpMessagingTemplate.convertAndSend("/topic/achievement", socket);

    }
    private Integer cashedTargetQuantity = null;
    private LocalDate targetDate = null;
    public int getTodayTarget(int minTarget, int maxTarget) {
        LocalDate today = LocalDate.now();

        if (cashedTargetQuantity == null || targetDate == null || !targetDate.equals(today)) {
            Random random = new Random(today.toEpochDay());
            cashedTargetQuantity = minTarget + 50 * random.nextInt((maxTarget - minTarget) / 50 + 1);
            targetDate = today;
        }
        return cashedTargetQuantity;
    }
}


