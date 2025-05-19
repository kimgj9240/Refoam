package com.example.refoam.dto;

import com.example.refoam.domain.MaterialName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPredictionInput {
    @NotEmpty(message = "날짜는 비워있을 수 없습니다.")
    private String date;

    @NotNull(message = "수량은 비워있을 수 없습니다")
    private Integer qty;

    @NotNull(message = "원재료는 비워있을 수 없습니다.")
    private MaterialName materialName2;
}
