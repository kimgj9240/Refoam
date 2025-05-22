package com.example.refoam.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private ProductName productName;

    private int orderQuantity;

    private LocalDateTime orderDate;

    // 주문 리스트 공정상태
    @Setter
    private String orderState; // 준비 중, 배합완료, 배합실패, 공정완료, 진행 중

    @OneToMany(mappedBy = "order")
    private List<ErrorStats> errorStateList = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "order")
    private List<Process> processList = new ArrayList<>();

    // 연관관계 매핑
    @Builder.Default // 기본값이 무시되지 않도록 보장하는 어노테이션
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMaterial> orderMaterials = new ArrayList<>();

    public void addOrderMaterial(OrderMaterial orderMaterial){
        orderMaterials.add(orderMaterial); // 1. 자식 리스트에 추가
        orderMaterial.setOrder(this); // 2. 자식의 부모 설정
    }
}
