package com.xhamera1.heart_failure_predictor.model.enums;

import java.util.Objects;

public enum SexEnum {

    FEMALE(0, "F", "Female"),
    MALE(1, "M", "Male");

    private final int numericValue;
    private final String code;
    private final String displayName;

    SexEnum(int numericValue, String code, String displayName) {
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

    public static SexEnum fromNumericValue(int numericValue) {
        for (SexEnum sex : SexEnum.values()) {
            if (sex.getNumericValue() == numericValue) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value for SexEnum: " + numericValue);
    }

    public static SexEnum fromCode(String code) {
        Objects.requireNonNull(code, "Code cannot be null");
        for (SexEnum sex : SexEnum.values()) {
            if (sex.getCode().equalsIgnoreCase(code)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Invalid code for SexEnum: " + code);
    }

    public static SexEnum fromDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        for (SexEnum sex : SexEnum.values()) {
            if (sex.getDisplayName().equalsIgnoreCase(displayName)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Invalid display name for SexEnum: " + displayName);
    }
}