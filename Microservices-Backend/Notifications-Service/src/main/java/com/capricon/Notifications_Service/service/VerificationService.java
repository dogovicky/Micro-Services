package com.capricon.Notifications_Service.service;

import com.capricon.Notifications_Service.dto.VerificationRequest;
import com.capricon.Notifications_Service.model.VerificationCode;
import com.capricon.Notifications_Service.repository.VerificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepo verificationRepo;

    public Boolean verifyAccount(VerificationRequest request) {
        try {
            // Find the code in the database & validate
            VerificationCode code = verificationRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Code not found"));

            if (code.getCode().equals(request.getCode())) {
                verificationRepo.delete(code);
                return true;
            }
            return false;
        } catch (Exception ex) {
            log.error("Error validating code, caused by: {}", ex.getCause().getMessage());
            return false;
        }
    }

}
