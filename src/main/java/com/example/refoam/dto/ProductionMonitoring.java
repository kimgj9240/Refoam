package com.example.refoam.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ProductionMonitoring {

    private LocalDate date;

    private int okCount;

    private int errCount;

    private int orderCount;
}
