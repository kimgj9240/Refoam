package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderMaterial {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material;

    // 주문 수량 - Material을 기준으로 가져온 수량
    private int deductedQuantity;
}
