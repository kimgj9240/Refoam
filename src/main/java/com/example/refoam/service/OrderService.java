package com.example.refoam.service;

import com.example.refoam.domain.*;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MaterialService materialService;
    private final AlertLogRepository alertLogRepository;

    // 주문 생성
    @Transactional
    public Long save(Orders order){
        int orderQuantity = order.getOrderQuantity();
        ProductName productName = order.getProductName();

        // 재고 충분 여부만 확인
        if(!materialService.isEnoughMaterial(productName, orderQuantity)){
            throw new IllegalStateException("재료가 부족합니다.");
        }

        // 주문 저장만 수행
        orderRepository.save(order);
        return order.getId();
    }



    // 단건 조회
    public Optional<Orders> findOneOrder(Long orderId){
        return orderRepository.findById(orderId);
    }

    // 전체 주문 조회
    public List<Orders> findOrders(){
        return orderRepository.findAll();
    }

    // 주문 삭제
    @Transactional
    public void deleteOrder(Long orderId){
        // 1. 주문 조회
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다, 주문 번호:" + orderId));

        // 배합이 이미 시작된 경우 삭제 불가
        if (!order.getOrderState().equals("준비 중")) {
            throw new IllegalStateException("배합이 시작된 주문은 삭제할 수 없습니다.");
        }

//        // 정확히 복구
//        // OrderMaterial 리스트는 주문할 때 기록한 '어떤 재고에서 얼마나 차감했는가'를 담고있다.
//        for(OrderMaterial orderMaterial : order.getOrderMaterials()){
//            //복구할 Material 꺼내기 - 차감했던 재고를 그대로 다시 가져옴
//            Material material = orderMaterial.getMaterial();
//            log.info("기존에 있던 재고 {}", material.getMaterialQuantity());
//            log.info("더할 재고 {}", orderMaterial.getDeductedQuantity());
//
//            // 예전에 차감했던 수량(deductedQuantity)만큼 더해서 원래대로 되돌림
//            material.setMaterialQuantity(material.getMaterialQuantity() + orderMaterial.getDeductedQuantity());
//            materialService.save(material);
//        }
        //주문 삭제
        orderRepository.deleteById(orderId);
    }

    // 페이지네이션 구현용 메서드
    public Page<Orders> getList(int page){
        // 최신순으로 보이게
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));

        PageRequest pageable = PageRequest.of(page, 12, Sort.by(sorts));

        return this.orderRepository.findAll(pageable);

    }

}
