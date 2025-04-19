package com.capricon.User_Service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {

    @NotBlank(message = "Message can't be null")
    private String email;

    @NotBlank(message = "Please enter validation code")
    private String code;
}
