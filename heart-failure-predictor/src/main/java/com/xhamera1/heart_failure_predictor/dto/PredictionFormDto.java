package com.xhamera1.heart_failure_predictor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionFormDto {

    @NotNull(message = "Age cannot be null")
    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age seems unrealistic, must be less than or equal to 120")
    private Integer age;

    @NotBlank(message = "Sex cannot be blank")
    private String sex;

    @NotBlank(message = "Chest Pain Type cannot be blank")
    private String chestPainType;

    @NotNull(message = "Resting BP cannot be null")
    @Min(value = 0, message = "Resting BP cannot be negative")
    private Integer restingBP;

    @NotNull(message = "Cholesterol cannot be null")
    @Min(value = 0, message = "Cholesterol cannot be negative")
    private Integer cholesterol;

    @NotNull(message = "Fasting BS cannot be null")
    @Min(value = 0, message = "Fasting BS must be 0 or 1")
    @Max(value = 1, message = "Fasting BS must be 0 or 1")
    private Integer fastingBS;

    @NotBlank(message = "Resting ECG cannot be blank")
    private String restingECG;

    @NotNull(message = "Max HR cannot be null")
    @Min(value = 1, message = "Max HR must be greater than 0")
    private Integer maxHR;

    @NotNull(message = "Exercise Angina cannot be null")
    @Min(value = 0, message = "Exercise Angina must be 0 or 1")
    @Max(value = 1, message = "Exercise Angina must be 0 or 1")
    private Integer exerciseAngina;

    @NotNull(message = "Oldpeak cannot be null")
    private Double oldpeak;

    @NotBlank(message = "ST Slope cannot be blank")
    private String stSlope;
}