package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class QualityCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean checkResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private Process process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualityCheckInput_id")
    private QualityCheckInput qualityCheckInput;
}
