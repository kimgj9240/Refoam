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

    private String getStatus(double errorRate) {
        if (errorRate <= 0.1) return "green-light";
        if (errorRate <= 0.3) return "yellow-light";
        return "red-light";
    }
}


