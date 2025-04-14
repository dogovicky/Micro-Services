package com.capricon.User_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordDTO {

    @NotBlank(message = "Password field must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be have at least one lowercase, one uppercase, " +
                    "one number one special character and must not be less than eight characters")
    @Size(min = 8, max = 20, message = "Minimum of eight and maximum of twenty characters")
    private String password;

}
