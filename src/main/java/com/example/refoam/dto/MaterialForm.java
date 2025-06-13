package com.example.refoam.dto;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.MaterialName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialForm {
    private Long id;
    @NotNull(message = "원재료 명은 필수입니다.")
    private MaterialName materialName;
    @NotNull(message = "수량은 필수입니다.")
    private Integer materialQuantity;
    private LocalDateTime materialDate;
    private Employee employee;
}
