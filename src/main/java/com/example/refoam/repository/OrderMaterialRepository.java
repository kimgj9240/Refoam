package com.example.refoam.repository;

import com.example.refoam.domain.Material;
import com.example.refoam.domain.OrderMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderMaterialRepository extends JpaRepository<OrderMaterial, Long> {
    // 참조하는 Material 존재하는 지 확인
    boolean existsByMaterial(Material material);

    // material을 참조하는 모든 주문 ID 리스트 반환
    @Query("SELECT om.order.id FROM OrderMaterial om WHERE om.material = :material")
    List<Long> findOrderIdsByMaterial(@Param("material") Material material);
}