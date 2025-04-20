package com.xhamera1.heart_failure_predictor.model.enums;

import java.util.Objects;

public enum ExerciseAnginaEnum {

    NO(0, "N", "No"),
    YES(1, "Y", "Yes");

    private final int numericValue;
    private final String code;
    private final String displayName;

    ExerciseAnginaEnum(int numericValue, String code, String displayName) {
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

    public static ExerciseAnginaEnum fromNumericValue(int numericValue) {
        for (ExerciseAnginaEnum type : ExerciseAnginaEnum.values()) {
            if (type.getNumericValue() == numericValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value for ExerciseAnginaEnum: " + numericValue);
    }

    public static ExerciseAnginaEnum fromCode(String code) {
        Objects.requireNonNull(code, "Code cannot be null");
        for (ExerciseAnginaEnum type : ExerciseAnginaEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code for ExerciseAnginaEnum: " + code);
    }

    public static ExerciseAnginaEnum fromDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        for (ExerciseAnginaEnum type : ExerciseAnginaEnum.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid display name for ExerciseAnginaEnum: " + displayName);
    }
}