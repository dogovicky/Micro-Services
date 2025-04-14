package com.capricon.User_Service.controller;


import com.capricon.User_Service.dto.*;
import com.capricon.User_Service.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/signup/basic_info")
    public ResponseEntity<ApiResponse<String>> saveBasicInfo(@Valid @RequestBody BasicInfoDTO basicInfoDTO) {
        try {
            log.info("Sign up process commenced successfully");
            ApiResponse<String> response = signUpService.saveBasicInfo(basicInfoDTO);
            if (response.isSuccess()) {
                log.info("Successfully saved basic information");
                return ResponseEntity.ok(response);
            } else {
                log.error("Error saving basic info");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleSignUpControllerException(ex);
        }
    }

    @PostMapping("/signup/create-password/{email}")
    public ResponseEntity<ApiResponse<String>> savePassword(
            @PathVariable String email,
            @Valid @RequestBody PasswordDTO passwordDTO) {
        try {
            ApiResponse<String> response = signUpService.savePassword(email, passwordDTO);
            if (response.isSuccess()) {
                log.info("Successfully saved password");
                return ResponseEntity.ok(response);
            } else {
                log.error("Error saving password");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleSignUpControllerException(ex);
        }
    }

    @PostMapping("/signup/save-profile-info/{email}")
    public ResponseEntity<ApiResponse<ResponseTokenDTO>> saveProfileInfo(@PathVariable String email, @Valid @RequestBody ProfileInfoDTO profileInfoDTO) {
        log.info("Finalizing sign up process for user: {}", email);
        try {
            ApiResponse<ResponseTokenDTO> response = signUpService.saveProfileInfo(email, profileInfoDTO);
            if (response.isSuccess()) {
                log.info("Successfully saved profile information");
                return ResponseEntity.ok(response);
            } else {
                log.error("Error saving profile info");
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleSignUpControllerException(ex);
        }
    }


    private <T> ResponseEntity<ApiResponse<T> > handleSignUpControllerException(Throwable ex) {
        Throwable cause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error processing request at controller level, caused by: {}", cause.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Saving process failed. Please try again"));
    }

}
