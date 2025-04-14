package com.capricon.User_Service.exception;

import com.capricon.User_Service.exception.enums.TechnicalErrorCode;
import com.capricon.User_Service.exception.enums.UserErrorCode;
import com.capricon.User_Service.exception.enums.ValidationErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {

//    @Test
//    void shouldThrowBaseExceptionWithCorrectMessage() {
//        BaseException exception = assertThrows(
//                BaseException.class,
//                () -> {
//                    throw new BaseException("Base exception thrown", TechnicalErrorCode.UNEXPECTED_ERROR);
//                }
//        );
//
//        assertEquals("Base exception thrown", exception.getMessage());
//    }

    @Test
    void shouldThrowValidationExceptionWithCorrectMessage() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    throw new ValidationException("Validation rule violated(Missing fields)",
                            ValidationErrorCode.REQUIRED_FIELD_MISSING);
                }
        );

        assertEquals("Validation rule violated(Missing fields)", exception.getMessage());
    }

    @Test
    void shouldThrowUserExceptionWithCorrectMessage() {
        UserException exception = assertThrows(
                UserException.class,
                () -> {
                    throw new UserException("User exception thrown", UserErrorCode.USER_NOT_FOUND);
                }
        );

        assertEquals("User exception thrown", exception.getMessage());
    }

}
