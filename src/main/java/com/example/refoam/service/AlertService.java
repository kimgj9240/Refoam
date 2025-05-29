package com.example.refoam.service;

import com.example.refoam.domain.AlertLog;
import com.example.refoam.domain.Employee;
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
    private final AlertLogRepository alertLogRepository;

    // 현재 로그인한 사용자의 안 읽은 알림 개수 조회
    public long countUnreadAlerts(Employee employee) {
        return alertLogRepository.countByEmployeeAndCheckedFalse(employee);
    }


    // 현재 로그인한 사용자의 안 읽은 알림 목록 조회

    public List<AlertLog> getUnreadAlerts(Employee employee) {
        return alertLogRepository.findAllByEmployeeAndCheckedFalse(employee);
    }

    // 알림 읽음 처리

    @Transactional
    public void markAsRead(Long alertId) {
        AlertLog alert = alertLogRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
        alert.setChecked(true);
    }

}
