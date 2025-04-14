package com.capricon.User_Service.exception.enums;

import com.capricon.User_Service.exception.ErrorCode;

public enum ValidationErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT("3001"),
    WEAK_PASSWORD("3002"),
    REQUIRED_FIELD_MISSING("3003"),
    INVALID_DATE_FORMAT("3004"),
    OTP_EXPIRED("3005");

    private final String code;
    private static final String CATEGORY = "VALIDATION";

    ValidationErrorCode(String code) {
        this.code = code;
    }


    @Override
    public String getCode() {
        return CATEGORY + "_" + code;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
