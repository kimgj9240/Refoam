package com.example.refoam.repository;

import com.example.refoam.domain.ErrorStatistics;
import com.example.refoam.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ErrorStatisticsRepository extends JpaRepository<ErrorStatistics, Long> {
    List<ErrorStatistics> findByOrder(Orders orders);
    //@Query("SELECT p FROM Process p WHERE p.order=:orderId and p.processDate >= :interval")
    @Query("SELECT e.errorCount FROM ErrorStatistics e WHERE e.order=:orderId")
    Integer findMaxErrorCountGroupedByOrderId(@Param("orderId") Orders orderId);

    @Query("SELECT e.errorCount, o.orderQuantity FROM ErrorStatistics e JOIN e.order o WHERE e.order=:orderId")
     ErrorStatistics findErrorCountAndOrderQuantity(@Param("orderId") Orders orderId);

}
