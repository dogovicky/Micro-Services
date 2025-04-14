package com.capricon.User_Service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Please enter your email address")
    private String email;

    //private String phoneNumber;

    @NotBlank(message = "Please fill in your password")
    private String password;


}
