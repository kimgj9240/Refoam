package com.example.refoam.repository;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findAllByOrder_Id(Long orderId);
    @Query("SELECT p FROM Process p WHERE p.order=:orderId and p.processDate >= :interval")
    List<Process> findByOrderAndProcessDateInterval(@Param("orderId") Orders orderId, @Param("interval") LocalDateTime interval);

    Page<Process> findAllByOrder_Id(Long orderId, Pageable pageable);
    List<Process> findByOrderAndProcessDateInterval(@Param("orderId") Orders orderId, @Param("interval")LocalDateTime interval);

    @Query("SELECT p FROM Process p " +
            "WHERE p.processDate BETWEEN :start AND :end " +
            "ORDER BY p.processDate, p.id")
    List<Process> findProcessesInDateRange(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
