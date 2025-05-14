package com.capricon.User_Service.service;

import com.capricon.User_Service.dto.ApiResponse;
import com.capricon.User_Service.dto.LoginRequest;
import com.capricon.User_Service.dto.ResponseTokenDTO;
import com.capricon.User_Service.exception.UserException;
import com.capricon.User_Service.exception.enums.UserErrorCode;
import com.capricon.User_Service.model.User;
import com.capricon.User_Service.repository.jpa.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    // Define all external dependencies used throughout the login process

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginService loginService;

    @Test
    void shouldLoginSuccessfullyWhenCorrectCredentialsProvided() {

        // Given -> Given a user who follows the login process
        String email = "test@example.com";
        String password = "password";
        String token = "jwt-token";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsEnabled(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsAccountNonExpired(true);

        // Build a login request
        LoginRequest request = new LoginRequest(email, password);

        // Perform mock processes (findByEmail, authenticate and jwt-generation)
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(user)).thenReturn(token);

        // When -> When the processes are executed, what happens then ?
        ApiResponse<ResponseTokenDTO> response = loginService.processLogin(request);

        // Then -> Assert that results returned are true and all methods are working correctly
        assertNotNull(response); // Verify that response is not null
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData()); // Verify the data returned is not null
        assertEquals(email, response.getData().getEmail());
        assertEquals(token, response.getData().getToken());


        // Verify interactions -> verify that the results returned match
        verify(userRepo).findByEmail(email);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);

    }


    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "nonexists@example.com";
        String password = "password";

        LoginRequest request = new LoginRequest(email, password);

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
            loginService.processLogin(request);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());


        verify(userRepo).findByEmail(email);
        verifyNoInteractions(authManager, jwtService);


    }

    @Test
    void shouldThrowExceptionWhenAccountIsDisabled() {

        // Given
        String email = "disabled@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsEnabled(false); // Account is disabled

        LoginRequest request = new LoginRequest(email, password);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
           loginService.processLogin(request);
        });

        assertEquals("Your account has not yet been validated. Please contact support team.", exception.getMessage());
        assertEquals(UserErrorCode.AUTHORIZATION_DENIED, exception.getErrorCode());

        verify(userRepo).findByEmail(email);
        verifyNoInteractions(authManager, jwtService);

    }

    @Test
    void shouldThrowExceptionWhenAccountIsExpired() {
        // Given
        String email = "expired@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(false); // Account is expired

        LoginRequest request = new LoginRequest(email, password);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
            loginService.processLogin(request);
        });

        assertEquals("Your account has expired. Please contact support.", exception.getMessage());
        assertEquals(UserErrorCode.AUTHORIZATION_DENIED, exception.getErrorCode());

        verify(userRepo).findByEmail(email);
        verifyNoInteractions(authManager, jwtService);
    }

    @Test
    void shouldThrowExceptionWhenAccountIsLocked() {
        // Given
        String email = "expired@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(false); // Account is locked

        LoginRequest request = new LoginRequest(email, password);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        // Whe & Then
        UserException exception = assertThrows(UserException.class, () -> {
            loginService.processLogin(request);
        });

        assertEquals("Your account is locked. Please contact support.", exception.getMessage());
        assertEquals(UserErrorCode.AUTHORIZATION_DENIED, exception.getErrorCode());

        verify(userRepo).findByEmail(email);
        verifyNoInteractions(authManager, jwtService);

    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // Given
        String email = "test@example.com";
        String password = "wrong-password";

        User user = new User();
        user.setEmail(email);
        user.setPassword("correct-password");
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);

        LoginRequest request = new LoginRequest(email, password);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
            loginService.processLogin(request);
        });

        assertEquals("Wrong password. Please try again.", exception.getMessage());
        assertEquals(UserErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());

        verify(userRepo).findByEmail(email);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);

    }

}
