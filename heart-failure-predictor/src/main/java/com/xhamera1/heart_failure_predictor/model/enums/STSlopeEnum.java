package com.xhamera1.heart_failure_predictor.model.enums;

import java.util.Objects;

public enum STSlopeEnum {

    DOWN(0, "Down", "Down"),
    FLAT(1, "Flat", "Flat"),
    UP(2, "Up", "Up");

    private final int numericValue;
    private final String code;
    private final String displayName;

    STSlopeEnum(int numericValue, String code, String displayName) {
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

    public static STSlopeEnum fromNumericValue(int numericValue) {
        for (STSlopeEnum type : STSlopeEnum.values()) {
            if (type.getNumericValue() == numericValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value for ST_SlopeEnum: " + numericValue);
    }

    public static STSlopeEnum fromCode(String code) {
        Objects.requireNonNull(code, "Code cannot be null");
        for (STSlopeEnum type : STSlopeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code for ST_SlopeEnum: " + code);
    }

    public static STSlopeEnum fromDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        for (STSlopeEnum type : STSlopeEnum.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid display name for ST_SlopeEnum: " + displayName);
    }
}