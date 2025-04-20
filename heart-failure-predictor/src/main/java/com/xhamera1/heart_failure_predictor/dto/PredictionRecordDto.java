package com.xhamera1.heart_failure_predictor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PredictionRecordDto {

    private Long id;
    private LocalDateTime predictionTimestamp;
    private Boolean predictedOutcome;
    private Integer age;
    private String sex;
    private String chestPainType;
    private Integer restingBP;
    private Integer cholesterol;
    private Boolean fastingBS;
    private String restingECG;
    private Integer maxHR;
    private String exerciseAngina;
    private Double oldpeak;
    private String stSlope;

}