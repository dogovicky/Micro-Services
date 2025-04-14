package com.capricon.User_Service.exception;

import com.capricon.User_Service.exception.enums.ValidationErrorCode;

public class ValidationException extends BaseException {
    public ValidationException(String message, ValidationErrorCode errorCode) {
        super(message, errorCode);
    }
}
