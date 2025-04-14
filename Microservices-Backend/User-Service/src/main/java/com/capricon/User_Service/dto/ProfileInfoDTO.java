package com.capricon.User_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfoDTO {

    @NotBlank(message = "Fill in your nationality")
    private String nationality;

   // @NotBlank(message = "Fill in your date of birth")
    private Date dateOfBirth;

    @NotBlank(message = "Fill in your government issued ID")
    @Size(min = 5, max = 20, message = "Enter a valid ID number")
    private String identificationNumber;

    @NotBlank(message = "Please input this field")
    private String street;

    @NotBlank(message = "Please input this field")
    private String city;

    @NotBlank(message = "Please input this field")
    private String state;

    @NotBlank(message = "Please input this field")
    private String zipCode;

}
