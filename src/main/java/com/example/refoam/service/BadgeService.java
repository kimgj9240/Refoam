package com.example.refoam.service;

import com.example.refoam.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.refoam.dto.ProductionMonitoring;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final MonitoringService monitoringService;

    /**
     * 전체 기간(최근 7일 등) 동안의 불량 건수를 합산해서 뱃지 숫자로 돌려줍니다.
     */
    /*public long getErrorBadgeCount() {
        return monitoringService.productionMonitorings()
                .stream()
                .mapToLong(ProductionMonitoring::getErrCount)  // DTO의 errCount 필드
                .sum();
    }*/
}

