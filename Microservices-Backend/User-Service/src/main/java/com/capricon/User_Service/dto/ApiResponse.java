package com.capricon.User_Service.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {

    private String responseId;

    private OffsetDateTime timestamp;

    private int status; //HTTP status code

    private boolean success; // Success flag

    private String message; // Main response message

    private String errorMessage; // Detailed error message

    private T data; // Payload of the response

    private List<ErrorDetail> errors;

    private String path; // Path to where error occurred

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ErrorDetail {
        private String field; // Field where error occurred
        private String message; //Error message
        private String code; // Error code
    }

    public ApiResponse() {
        // Automatically generate the responseId (UUID) and timestamp when the object is created
        this.responseId = String.valueOf(UUID.randomUUID().getMostSignificantBits());  // You can customize this logic
        this.timestamp = OffsetDateTime.now();  // Sets the current time for the timestamp
    }


    // Static method to create a successful response
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Request successful")
                .data(data)
                .build();
    }

    // Static method with successful response and custom message
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .errorMessage(message)
                .build();
    }

    // Static method to create an error response
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .path(path)
                .errorMessage(message)
                .build();
    }

    // Static method to create an error response with validation errors
    public static <T> ApiResponse<T> error(HttpStatus status, String message, List<ErrorDetail> errors, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .errorMessage(message)
                .path(path)
                .errors(errors)
                .build();
    }

    public Boolean isSuccess() {
        return success;
    }

}
