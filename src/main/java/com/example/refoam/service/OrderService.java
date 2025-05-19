package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MaterialService materialService;

    // 주문 생성
    @Transactional
    public Long save(Orders order){
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
        orderRepository.deleteById(orderId);
    }
}
