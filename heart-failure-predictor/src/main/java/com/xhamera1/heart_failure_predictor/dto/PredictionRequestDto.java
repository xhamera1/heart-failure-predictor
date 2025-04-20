package com.xhamera1.heart_failure_predictor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDto {

    private Integer age;
    private Integer sex;
    private Integer chestPainType;
    private Integer restingBP;
    private Integer cholesterol;
    private Integer fastingBS;
    private Integer restingECG;
    private Integer maxHR;
    private Integer exerciseAngina;
    private Double  oldPeak;
    private Integer stSlope;

}