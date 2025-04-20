package com.xhamera1.heart_failure_predictor.model;

import com.xhamera1.heart_failure_predictor.model.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prediction_records")
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SexEnum sex;

    @Enumerated(EnumType.STRING)
    @Column(name = "chest_pain_type", nullable = false, length = 20)
    private ChestPainTypeEnum chestPainType;

    @Column(name = "resting_bp", nullable = false)
    private Integer restingBP;

    @Column(nullable = false)
    private Integer cholesterol;

    @Column(name = "fasting_bs", nullable = false)
    private Boolean fastingBS;

    @Enumerated(EnumType.STRING)
    @Column(name = "resting_ecg", nullable = false, length = 10)
    private RestingECGEnum restingECG;

    @Column(name = "max_hr", nullable = false)
    private Integer maxHR;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_angina", nullable = false, length = 5)
    private ExerciseAnginaEnum exerciseAngina;

    @Column(nullable = false)
    private Double oldpeak;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_slope", nullable = false, length = 5)
    private STSlopeEnum stSlope;

    @Column(name = "predicted_heart_disease", nullable = false)
    private Boolean predictedHeartDisease;

    @Column(name = "prediction_timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime predictionTimestamp;

}