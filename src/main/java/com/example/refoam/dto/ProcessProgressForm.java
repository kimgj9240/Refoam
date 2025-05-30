package com.example.refoam.dto;

import com.example.refoam.domain.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProcessProgressForm {
    private Long orderId;
    private int completedCount;
    private int totalCount;
    private double errorRate;
    private String status;
}