package com.xhamera1.heart_failure_predictor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponseDto {

    private Integer prediction;
    private Double probability_0;
    private Double probability_1;

}