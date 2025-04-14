package com.capricon.User_Service.exception;

import com.capricon.User_Service.exception.enums.UserErrorCode;

public class UserException extends BaseException {
    public UserException(String message, UserErrorCode errorCode) {
        super(message, errorCode);
    }
}
