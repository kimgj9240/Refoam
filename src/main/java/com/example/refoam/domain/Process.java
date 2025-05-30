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
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long id;

    @Setter
    private String lotNumber;

    @Setter
    private String status;

    @Setter
    private LocalDateTime processDate;

    // standard <-> process 간 양방향 연결 ( 1 : 1 )
    @Setter
    @OneToOne
    @JoinColumn(name = "standard_id")
    private Standard standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

}
