package com.example.refoam.repository;

import com.example.refoam.domain.AlertLog;
import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
    boolean existsByOrderAndCheckedFalse(Orders order);
    long countByEmployeeAndCheckedFalse(Employee employee);
    List<AlertLog> findAllByEmployeeAndCheckedFalse(Employee employee);

    // 읽음 처리
    @Modifying
    @Query("UPDATE AlertLog a SET a.checked = true WHERE a.id = :id")
    void markAsRead(@Param("id") Long id);
}
