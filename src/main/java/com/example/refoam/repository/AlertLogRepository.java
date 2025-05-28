package com.example.refoam.repository;

import com.example.refoam.domain.AlertLog;
import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
    // 사용자 기준으로 읽지 않은 알림 전체 조회
    List<AlertLog> findAllByEmployeeAndCheckedFalse(Employee employee);

    // 읽지 않은 알림 개수를 사용자 단위로 카운트
    long countByEmployeeAndCheckedFalse(Employee employee);

    // 전역에서 읽지 않은 알림 존재 여부 확인
    boolean existsByCheckedFalse();

    // 특정 주문에 대해 읽지 않은 알림이 있는지 확인 (중복 생성 방지)
    boolean existsByOrderAndCheckedFalse(Orders order);

    // 읽음 처리 (checked= false -> true)
    @Transactional
    @Modifying
    @Query("UPDATE AlertLog a SET a.checked = true WHERE a.id = :id")
    void markAsRead(@Param("id") Long id);
}
