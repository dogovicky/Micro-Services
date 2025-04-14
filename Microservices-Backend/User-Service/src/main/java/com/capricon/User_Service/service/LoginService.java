package com.capricon.User_Service.service;

import com.capricon.User_Service.dto.ApiResponse;
import com.capricon.User_Service.dto.LoginRequest;
import com.capricon.User_Service.dto.ResponseTokenDTO;
import com.capricon.User_Service.exception.UserException;
import com.capricon.User_Service.exception.enums.UserErrorCode;
import com.capricon.User_Service.model.User;
import com.capricon.User_Service.repository.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepo userRepo;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public CompletableFuture<ApiResponse<ResponseTokenDTO>> login(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> processLogin(request))
                .exceptionally(this::handleLoginServiceException);
    }

    @Transactional
    public ApiResponse<ResponseTokenDTO> processLogin(LoginRequest request) {
        // Step 1: Check if user exists in the db
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("User not found", UserErrorCode.USER_NOT_FOUND));

        // Step 2: Check account constraints
        if (!user.getIsEnabled()) {
            throw new UserException("Your account has not yet been validated. Please contact support team.",
                    UserErrorCode.AUTHORIZATION_DENIED);
        }

        if (!user.getIsAccountNonExpired()) {
            throw new UserException("Your account has expired. Please contact support.", UserErrorCode.AUTHORIZATION_DENIED);
        }
        if (!user.getIsAccountNonLocked()) {
            throw new UserException("Your account is locked. Please contact support.", UserErrorCode.AUTHORIZATION_DENIED);
        }

        try {
            // Step 3: Authenticate user & generate token
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            String authToken = jwtService.generateToken(user);

            // Step 4: Build API Response
            ResponseTokenDTO responseTokenDTO = new ResponseTokenDTO(user.getEmail(), authToken);
            return ApiResponse.success(responseTokenDTO, "Login successful");
        } catch (BadCredentialsException ex) {
            throw new UserException("Wrong password. Please try again.", UserErrorCode.AUTHENTICATION_FAILED);
        }

    }

    private <T> ApiResponse<T> handleLoginServiceException(Throwable ex) {
        Throwable cause = ex;
        while (cause instanceof CompletionException || cause instanceof ExecutionException) {
            cause = cause.getCause();
        }
        log.error("Error logging in user, caused by: {}", cause.getMessage());

        if (cause instanceof UserException userException) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, userException.getMessage());
        } else {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error occurred. Please contact website admin.");
        }
    }

}
