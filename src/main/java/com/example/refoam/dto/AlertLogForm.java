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
    private String materialName;
    private String message;

    public static AlertLogForm from(AlertLog alert) {
        return new AlertLogForm(
                alert.getId(),
                alert.getOrder() != null ? alert.getOrder().getId() : null,
                alert.getMaterial() != null ? alert.getMaterial().getId() : null,
                alert.getMaterial() != null ? alert.getMaterial().getMaterialName().name() : null,
                alert.getMessage()
        );
    }
}
