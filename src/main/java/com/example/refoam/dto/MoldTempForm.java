package com.example.refoam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MoldTempForm {
    private LocalDateTime time;
    private double value;
}
