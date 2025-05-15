package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class PredictionOrderInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String date;

    @Setter
    private int qty;

    @Setter
    @Enumerated(EnumType.STRING)
    private MaterialName materialName;

    @Setter
    @ManyToOne
    @JoinColumn(name = "record_id")
    private PredictionRecord record;
}
