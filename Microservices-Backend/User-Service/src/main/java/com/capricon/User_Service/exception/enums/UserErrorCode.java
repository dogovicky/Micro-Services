package com.capricon.User_Service.exception.enums;

import com.capricon.User_Service.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS("2001"),
    USER_NOT_FOUND("2002"),
    ACCOUNT_SUSPENDED("2003"),
    AUTHENTICATION_FAILED("2004"),
    AUTHORIZATION_DENIED("2005");

    private final String code;
    private static final String CATEGORY = "USER";

    UserErrorCode(String code) {
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
