package com.example.refoam.repository;

import com.example.refoam.domain.ErrorStatistics;
import com.example.refoam.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ErrorStatisticsRepository extends JpaRepository<ErrorStatistics, Long> {
    List<ErrorStatistics> findByOrder(Orders orders);

}
