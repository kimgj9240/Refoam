package com.example.refoam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductChartDataForm {
    private String productName;
    private long okCount;
    private long errorCount;
    private LocalDateTime timestamp;
}
