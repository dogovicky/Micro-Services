package com.capricon.User_Service.client;


import com.capricon.User_Service.dto.VerificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATIONS-SERVICE", url = "http://localhost:8084/api/verification")
public interface ValidationClient {

    @PostMapping("/verify-code")
    public Boolean verifyCode(@RequestBody VerificationRequest request);

}
