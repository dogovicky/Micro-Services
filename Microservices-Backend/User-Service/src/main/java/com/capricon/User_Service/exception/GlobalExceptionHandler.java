package com.capricon.User_Service.exception;

import com.capricon.User_Service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<String, HttpStatus> CATEGORY_STATUS_MAP = new HashMap<>();

    //Initialize default mappings between error codes and HTTP status codes
    static {
        CATEGORY_STATUS_MAP.put("TECHNICAL", HttpStatus.INTERNAL_SERVER_ERROR);
        CATEGORY_STATUS_MAP.put("USER", HttpStatus.UNAUTHORIZED);
        CATEGORY_STATUS_MAP.put("VALIDATION", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseExceptions(BaseException exception, WebRequest request) {
        String path = request.getDescription(false);
        if (path.startsWith("uri=")) {
            path = path.substring(4);
        }

        String category = exception.getErrorCode().getCategory();
        HttpStatus status = CATEGORY_STATUS_MAP.getOrDefault(category, HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(status).body(ApiResponse.error(status, exception.getMessage(), path));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //For handling exceptions thrown by @Valid annotation
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               WebRequest request) {

        String path = request.getDescription(false);
        if (path.startsWith("uri=")) {
            path = path.substring(4);
        }

        //Collect validation errors
        List<ApiResponse.ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ApiResponse.ErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .code(fieldError.getCode()) // e.g., "NotBlank", "Size"
                        .build()
                )
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Validation Error", errors, path));

    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ApiResponse<Object>> handleTechnicalException(TechnicalException ex, WebRequest request) {
        return handleBaseExceptions(ex, request);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(UserException ex, WebRequest request) {
        return handleBaseExceptions(ex, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex, WebRequest request) {
        return handleBaseExceptions(ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(Exception ex, WebRequest request) {
        String path = request.getDescription(false);
        if (path.startsWith("uri=")) {
            path = path.substring(4);
        }
        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected Error Occurred",
                path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
