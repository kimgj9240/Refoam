package com.example.refoam.repository;

import com.example.refoam.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByOrderStateAndStatisticsIntervalCheck(String orderState,boolean statisticsIntervalCheck);// 준비 중, 배합완료, 배합실패, 공정완료, 진행 중
    @Query("SELECT o FROM Orders o JOIN FETCH o.employee WHERE o.orderState = :orderState AND o.statisticsIntervalCheck = :statisticsIntervalCheck AND o.smtpCheck = :smtpCheck")
    List<Orders> findAllByOrderStateAndStatisticsIntervalCheckAndSmtpCheck(String orderState,boolean statisticsIntervalCheck,boolean smtpCheck);// 준비 중, 배합완료, 배합실패, 공정완료, 진행 중

    //공정 건수 차트용
    List<Orders> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Orders o WHERE DATE(o.orderDate) = CURRENT_DATE AND o.orderState = '배합실패' ")
    List<Orders> findMixFail();

    @Query("SELECT  o FROM Orders o WHERE  DATE(o.orderDate) = CURRENT_DATE ")
    List<Orders> findTodayOrders();

    List<Orders> findAllByOrderState(String orderState);
}
