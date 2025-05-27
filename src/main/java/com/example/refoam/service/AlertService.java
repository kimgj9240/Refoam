package com.example.refoam.service;

import com.example.refoam.domain.ErrorStatistics;
import com.example.refoam.domain.Orders;
import com.example.refoam.repository.ErrorStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final ErrorStatisticsRepository errorStatisticsRepository;
    private final OrderService orderService;

    private final double THRESHOLD = 0.3; // 기준값

    public boolean isCountExceeded() {
        List<Orders> orders = orderService.findOrders();
        for(Orders order : orders){
            ErrorStatistics errorStatistics = errorStatisticsRepository.findErrorCountAndOrderQuantity(order);
            int errorCount = errorStatistics.getErrorCount();
            int orderQuantity = errorStatistics.getOrder().getOrderQuantity();
            // 0으로 나누는 예외 방지
            if (orderQuantity > 0) {
                double errorRate = (double) errorCount / orderQuantity;
                if (errorRate >= THRESHOLD) {
                    return true;
                }
            }
        }
    return false;
    }
}
