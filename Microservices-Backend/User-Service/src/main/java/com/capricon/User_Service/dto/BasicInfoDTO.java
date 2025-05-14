package com.capricon.User_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfoDTO {

    @NotBlank(message = "Please enter your full name")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
//    @Pattern(regexp = "\\^[0-9]{10}^$", message = "Please enter a valid phone number")
    private String phoneNumber;

}
