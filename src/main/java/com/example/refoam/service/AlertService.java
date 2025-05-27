package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.repository.ErrorStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final ErrorStatisticsRepository errorStatisticsRepository;
    private final OrderService orderService;
    private final AlertLogRepository alertLogRepository;

    private static final double THRESHOLD = 0.3;

    public boolean isAnyOrderExceeded() {
        List<Orders> orders = orderService.findOrders();
        for (Orders order : orders) {
            int errorCount = Optional.ofNullable(
                    errorStatisticsRepository.findMaxErrorCountGroupedByOrderId(order)
            ).orElse(0);
            int quantity = order.getOrderQuantity();

            if (quantity > 0 && (double) errorCount / quantity >= THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    public void markAsRead(Long alertId){
        alertLogRepository.markAsRead(alertId);
    }
}
