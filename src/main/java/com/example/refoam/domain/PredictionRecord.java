package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class PredictionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private double prediction;

    @Setter
    private LocalDateTime predictedAt;

    @Lob // 긴 Json 저장을 위한 어노테이션
    @Setter
    private String inputData;

    @Setter
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<PredictionOrderInput> orderInputs;
}
