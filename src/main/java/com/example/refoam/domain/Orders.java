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
    private String orderState;


    @OneToMany(mappedBy = "order")
    private List<ErrorStats> errorStateList = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "order")
    private List<Process> processList = new ArrayList<>();

}
