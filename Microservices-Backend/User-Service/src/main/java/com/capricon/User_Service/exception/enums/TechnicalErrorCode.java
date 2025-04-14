package com.capricon.User_Service.exception.enums;

import com.capricon.User_Service.exception.ErrorCode;

public enum TechnicalErrorCode implements ErrorCode {
    DATABASE_CONNECTION_FAILED("1001"), //Database connection failures
    SERVICE_UNAVAILABLE("1002"), //Another microservice is down
    UNEXPECTED_ERROR("1003"); //Handle unexpected errors

    private final String code;
    private static final String CATEGORY = "TECHNICAL";

    TechnicalErrorCode(String code) {
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
