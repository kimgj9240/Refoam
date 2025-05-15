package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long id;

    @Setter
    private String step;

    @Setter
    private String status;

    @Setter
    private LocalDateTime processDate;

    @OneToMany(mappedBy = "process")
    private List<Label> labels = new ArrayList<Label>();

    @OneToMany(mappedBy = "process")
    private List<QualityCheck> qualityChecks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id")
    private Standard standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

}
