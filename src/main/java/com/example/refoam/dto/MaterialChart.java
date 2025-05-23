package com.example.refoam.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MaterialChart {
    private LocalDate date;
    private int totalQuantity;

}
