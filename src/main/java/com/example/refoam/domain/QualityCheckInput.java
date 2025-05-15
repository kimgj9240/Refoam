package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class QualityCheckInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String inputData;

    @OneToMany(mappedBy = "qualityCheckInput")
    private List<QualityCheck> qualityChecks = new ArrayList<>();

}
