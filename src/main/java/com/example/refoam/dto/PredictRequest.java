package com.example.refoam.dto;

import com.example.refoam.domain.MaterialName;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;
@Data
public class PredictRequest {
    @Valid  //리스트 내부 요소까지 유효성 검사 적용
    private List<OrderPredictionInput> orders;
}
