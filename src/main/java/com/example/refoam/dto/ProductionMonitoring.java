package com.example.refoam.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductionMonitoring {

    private String product;

    private LocalDate date;

    private int okCount;

    private int errCount;

    private int errTempCount;

    private int errTimeCount;

    private int orderCount;

    private int mixFailCount;   // 배합실패
}
