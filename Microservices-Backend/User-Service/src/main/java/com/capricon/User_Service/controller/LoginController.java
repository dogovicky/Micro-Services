package com.capricon.User_Service.controller;

import com.capricon.User_Service.dto.ApiResponse;
import com.capricon.User_Service.dto.LoginRequest;
import com.capricon.User_Service.dto.ResponseTokenDTO;
import com.capricon.User_Service.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ApiResponse<ResponseTokenDTO>>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Logging in user: {}", request.getEmail());
        return loginService.login(request).thenApply(response -> {
            if (response.isSuccess()) {
                log.info("Successfully logged in user");
                return ResponseEntity.ok(response);
            } else {
                log.error("Error logging in user");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }).exceptionally(this::handleLoginControllerException);
    }


    private <T> ResponseEntity<ApiResponse<T>> handleLoginControllerException(Throwable ex) {
        Throwable cause = (ex.getCause() instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error logging in user at controller level. Caused by: {}", cause.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while logging in."));
    }

}
