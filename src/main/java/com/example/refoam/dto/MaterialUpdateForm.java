package com.example.refoam.dto;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.MaterialName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUpdateForm {
    private long id;
    private MaterialName materialName;
    @NotNull(message = "수량은 필수입니다.")
    private int materialQuantity;
    private LocalDateTime materialDate;
    private Employee employee;
}
