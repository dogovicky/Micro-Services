package com.capricon.User_Service.controller;

import com.capricon.User_Service.dto.ApiResponse;
import com.capricon.User_Service.dto.BasicInfoDTO;
import com.capricon.User_Service.service.SignUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SignUpController.class)
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SignUpService signUpService;

    @InjectMocks
    private SignUpController signUpController;


    @Test
    void saveBasicInfoTest_SuccessfulCase() throws Exception {
        // Arrange
        BasicInfoDTO basicInfoDTO = new BasicInfoDTO("Dogo Vicky", "test@example.com", "25412345678");

        //Mock the service method to return the expected response
        Mockito.doReturn(ResponseEntity.ok(ApiResponse.success(basicInfoDTO.getEmail(), "Proceed to next step")))
                .when(signUpService).saveBasicInfo(Mockito.any(BasicInfoDTO.class));


        // Act and Assert
        mockMvc.perform(post("/auth/signup/basic_info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(basicInfoDTO)))
                .andExpect(status().isOk())
                .andExpectAll(content().string(basicInfoDTO.getEmail()),
                        content().string("Proceed to next step"));

        //Verify that the service method was called
        Mockito.verify(signUpService).saveBasicInfo(Mockito.any(BasicInfoDTO.class));

    }

}
