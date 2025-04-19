package com.capricon.Notifications_Service.controller;

import com.capricon.Notifications_Service.dto.VerificationRequest;
import com.capricon.Notifications_Service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyAccount(@RequestBody VerificationRequest request) {
        try {
            Boolean isVerified = verificationService.verifyAccount(request);
            if (isVerified) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }

        } catch (Exception ex) {
            log.error("Error validating code at controller level, caused by: {}", ex.getCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}
