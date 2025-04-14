package com.capricon.User_Service.exception;

import com.capricon.User_Service.exception.enums.TechnicalErrorCode;

public class TechnicalException extends BaseException {
    public TechnicalException(String message, TechnicalErrorCode errorCode) {
        super(message, errorCode);
    }
}
