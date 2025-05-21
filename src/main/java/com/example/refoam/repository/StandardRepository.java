package com.example.refoam.repository;

import com.example.refoam.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardRepository extends JpaRepository<Standard, Long> {
    //주문 번호로 공정 결과 모두 가져오기
    //List<Standard> findAllByOrderId(Long orderId);
}
