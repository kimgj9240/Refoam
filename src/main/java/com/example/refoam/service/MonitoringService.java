package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.dto.ProductionMonitoring;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final OrderRepository orderRepository;
    private final ProcessRepository processRepository;

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

                    // DTO 반환
                    return new ProductionMonitoring(date, okCount, errCount, orderCount);
                })
                // 날짜 오름차순 정렬
                .sorted(Comparator.comparing(ProductionMonitoring::getDate))
                .collect(Collectors.toList());

    }
}
