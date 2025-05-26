package com.example.refoam.repository;

import com.example.refoam.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByOrderStateAndStatisticsIntervalCheck(String orderState,boolean statisticsIntervalCheck);// 준비 중, 배합완료, 배합실패, 공정완료, 진행 중
}
