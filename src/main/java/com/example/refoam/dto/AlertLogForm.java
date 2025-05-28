package com.example.refoam.dto;

import com.example.refoam.domain.AlertLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertLogForm {
    private Long id;
    private Long orderId;
    private Long materialId;
    private String message;

    public static AlertLogForm from(AlertLog alert) {
        return new AlertLogForm(
                alert.getId(),
                alert.getOrder().getId(),
                alert.getMaterial().getId(),
                alert.getMessage()
        );
    }
}
