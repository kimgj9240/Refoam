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

    public String getStatusClass() {
        if (completedCount == 0) return "";
        if (errorRate <= 0.0) return "green-light";
        if (errorRate <= 0.1) return "yellow-light";
        return "red-light";
    }
}