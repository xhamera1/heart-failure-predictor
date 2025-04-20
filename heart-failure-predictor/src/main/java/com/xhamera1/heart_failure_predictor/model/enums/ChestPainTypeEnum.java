package com.xhamera1.heart_failure_predictor.model.enums;

import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;

import java.util.Objects;

public enum ChestPainTypeEnum {

    ASYMPTOMATIC(0, "ASY", "Asymptomatic"),
    ATYPICAL_ANGINA(1, "ATA", "Atypical Angina"),
    NON_ANGINAL_PAIN(2, "NAP", "Non-Anginal Pain"),
    TYPICAL_ANGINA(3, "TA", "Typical Angina");

    private final int numericValue;
    private final String code;
    private final String displayName;

    ChestPainTypeEnum(int numericValue, String code, String displayName) {
        this.numericValue = numericValue;
        this.code = code;
        this.displayName = displayName;
    }

    public int getNumericValue() {
        return numericValue;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ChestPainTypeEnum fromNumericValue(int numericValue) {
        for (ChestPainTypeEnum type : ChestPainTypeEnum.values()) {
            if (type.getNumericValue() == numericValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value for ChestPainTypeEnum: " + numericValue);
    }

    public static ChestPainTypeEnum fromCode(String code) {
        Objects.requireNonNull(code, "Code cannot be null");
        for (ChestPainTypeEnum type : ChestPainTypeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code for ChestPainTypeEnum: " + code);
    }

    public static ChestPainTypeEnum fromDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        for (ChestPainTypeEnum type : ChestPainTypeEnum.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid display name for ChestPainTypeEnum: " + displayName);
    }
}