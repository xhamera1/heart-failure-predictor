package com.xhamera1.heart_failure_predictor.model.enums;

import com.xhamera1.heart_failure_predictor.exceptions.ResourceNotFoundException;

import java.util.Objects;

public enum RestingECGEnum {

    LVH(0, "LVH", "LVH"), // Left Ventricular Hypertrophy
    NORMAL(1, "Normal", "Normal"),
    ST(2, "ST", "ST"); // ST-T wave abnormality

    private final int numericValue;
    private final String code;
    private final String displayName;

    RestingECGEnum(int numericValue, String code, String displayName) {
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

    public static RestingECGEnum fromNumericValue(int numericValue){
        for (RestingECGEnum type : RestingECGEnum.values()) {
            if (type.getNumericValue() == numericValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value for RestingECGEnum: " + numericValue);
    }

    public static RestingECGEnum fromCode(String code) {
        Objects.requireNonNull(code, "Code cannot be null");
        for (RestingECGEnum type : RestingECGEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code for RestingECGEnum: " + code);
    }

    public static RestingECGEnum fromDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        for (RestingECGEnum type : RestingECGEnum.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid display name for RestingECGEnum: " + displayName);
    }
}