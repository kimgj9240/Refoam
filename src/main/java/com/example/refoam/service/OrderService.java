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
        // 사용자가 주문한 제품과 수량을 가져온다
        int orderQuantity = order.getOrderQuantity(); // 주문 수량
        ProductName productName = order.getProductName();

        // 재고가 충분한지 확인한다
        if(!materialService.isEnoughMaterial(productName, orderQuantity)){
            throw new IllegalStateException("재료가 부족합니다.");
        }

        // 해당 제품에 필요한 원재료 목록을 조회한다.
        Map<MaterialName, Long> requiredMaterialStock = materialService.getRequiredMaterialStock(productName);
        log.info("원자재 재고수량 {}",requiredMaterialStock);

        // 원재료별로 실제 재고 차감
        for(Map.Entry<MaterialName, Long> entry : requiredMaterialStock.entrySet()){
            MaterialName materialName = entry.getKey();
            //주문 수량
            long requiredOrderQuantity = orderQuantity;

            log.info("entry - 원재료이름{}", entry.getKey());
            log.info("entry - 원자재 재고수량 {}", entry.getValue());
            log.info("orderQuantity 주문수량 {}", requiredOrderQuantity);

            // DB에서 해당 원재료 가져오기(여러 개 가져오기), 재고 리스트 조회
            List<Material> materials = materialService.findMaterialName(materialName);

            if(materials.isEmpty()){
                throw new IllegalStateException(materialName + "재료가 존재하지 않습니다.");
            }

            // 필요한 만큼 재료 차감 (선입선출 방식)
            for(Material material : materials){
                if(requiredOrderQuantity <= 0) break;

                long materialQuantity = material.getMaterialQuantity();

                //남은 재고와 주문 수량 비교하여 작은값을 가져온다
                long minQuantity = Math.min(materialQuantity, requiredOrderQuantity);

                //남은 재고에서 minQuantity를 빼고 다시 남은 재고의 값 수정
                material.setMaterialQuantity((int) (materialQuantity-minQuantity));


                log.info("차감된 원재료 : {}, 차감량 {}, 남은 주문 필요량 {}",materialName,minQuantity, requiredOrderQuantity);

                //(int) (materialQuantity-minQuantity) 값이 100이하면 알람 발생

                if ((int) (materialQuantity-minQuantity) <= 100) { // 알림 발생 에러율 조건
                    boolean alreadyAlerted = alertLogRepository.existsByMaterialAndCheckedFalse(material);
                    if (!alreadyAlerted) {
                        AlertLog alert = AlertLog.builder()
                                .material(material)
                                .employee(material.getEmployee())
                                .message("원재료 " + material.getMaterialName() + "수량이 100개 이하 입니다.")
                                .checked(false)
                                .createdDate(LocalDateTime.now())
                                .build();
                        alertLogRepository.save(alert);
                        log.info("원재료 알림 발생 : {}", material.getMaterialName());
                    }
                }


                materialService.save(material);

                //어떤 Material에서 얼마나 차감했는지 기록
                // Orders <-> OrderMaterial 양방향 연관관계 설정
                OrderMaterial orderMaterial = new OrderMaterial();
                orderMaterial.setMaterial(material);
                orderMaterial.setDeductedQuantity((int) minQuantity);
                order.addOrderMaterial(orderMaterial);

                // 주문 수량 업데이트
                // 차감된 수량만큼 남은 주문 수량 갱신
                requiredOrderQuantity -= minQuantity;
            }
            if(requiredOrderQuantity > 0){
                throw new IllegalStateException(materialName + "재료가 부족하여 차감할 수 없습니다.");
            }
        }
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

        // 정확히 복구
        // OrderMaterial 리스트는 주문할 때 기록한 '어떤 재고에서 얼마나 차감했는가'를 담고있다.
        for(OrderMaterial orderMaterial : order.getOrderMaterials()){
            //복구할 Material 꺼내기 - 차감했던 재고를 그대로 다시 가져옴
            Material material = orderMaterial.getMaterial();
            log.info("기존에 있던 재고 {}", material.getMaterialQuantity());
            log.info("더할 재고 {}", orderMaterial.getDeductedQuantity());

            // 예전에 차감했던 수량(deductedQuantity)만큼 더해서 원래대로 되돌림
            material.setMaterialQuantity(material.getMaterialQuantity() + orderMaterial.getDeductedQuantity());
            materialService.save(material);
        }
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
