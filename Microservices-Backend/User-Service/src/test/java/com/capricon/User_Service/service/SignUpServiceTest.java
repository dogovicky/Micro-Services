package com.capricon.User_Service.service;

import com.capricon.User_Service.client.ValidationClient;
import com.capricon.User_Service.dto.ApiResponse;
import com.capricon.User_Service.dto.BasicInfoDTO;
import com.capricon.User_Service.repository.jpa.RolesRepo;
import com.capricon.User_Service.repository.jpa.UserProfileRepo;
import com.capricon.User_Service.repository.jpa.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SignUpServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserProfileRepo profileRepo;

    @Mock
    private RolesRepo rolesRepo;

    @Mock
    private JwtService jwtService;

    @Mock
    private ValidationClient validationClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private SignUpService signUpService;

    @BeforeEach
    void setUp() {
        // Set up the mocked hash operations to be returned by redis template
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    // We test each step independently

    // Step 1: Basic info saving
    @Test
    @DisplayName("Basic Info: Should save basic info into redis when user does not exist")
    void shouldSaveBasicInfoWhenUserDoesNotExist() {
        // Given
        BasicInfoDTO basicInfo = new BasicInfoDTO();
        basicInfo.setFullName("John Smith");
        basicInfo.setEmail("test@example.com");
        basicInfo.setPhoneNumber("254 123456789");

        // Set up a redis key
        String redisKey = "signup:" + basicInfo.getEmail();

        when(userRepo.existsByEmail(basicInfo.getEmail())).thenReturn(false);

        // When
        ApiResponse<String> response = signUpService.saveBasicInfo(basicInfo);

        // Then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Proceed to next step", response.getMessage());
        assertEquals(basicInfo.getEmail(), response.getData());

        // Verify redis interactions
        verify(hashOperations).put(eq(redisKey), eq("fullName"), eq(basicInfo.getFullName()));
        verify(hashOperations).put(eq(redisKey), eq("email"), eq(basicInfo.getEmail()));
        verify(hashOperations).put(eq(redisKey), eq("phoneNumber"), eq(basicInfo.getPhoneNumber()));
        verify(redisTemplate).expire(eq(redisKey), eq(Duration.ofMinutes(30)));

    }

}
