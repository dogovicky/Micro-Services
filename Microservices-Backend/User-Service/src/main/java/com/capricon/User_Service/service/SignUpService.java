package com.capricon.User_Service.service;

import com.capricon.User_Service.client.ValidationClient;
import com.capricon.User_Service.dto.*;
import com.capricon.User_Service.exception.TechnicalException;
import com.capricon.User_Service.exception.UserException;
import com.capricon.User_Service.exception.ValidationException;
import com.capricon.User_Service.exception.enums.UserErrorCode;
import com.capricon.User_Service.model.Address;
import com.capricon.User_Service.model.Role;
import com.capricon.User_Service.model.User;
import com.capricon.User_Service.model.UserProfile;
import com.capricon.User_Service.model.enums.RoleType;
import com.capricon.User_Service.repository.jpa.RolesRepo;
import com.capricon.User_Service.repository.jpa.UserProfileRepo;
import com.capricon.User_Service.repository.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepo userRepo;
    private final UserProfileRepo userProfileRepo;
    private final RolesRepo rolesRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ValidationClient validationClient;


    //Step 1: Save basic info
    public ApiResponse<String> saveBasicInfo(BasicInfoDTO basicInfoDTO) {

        try {
            Boolean isUserRegistered = userRepo.existsByEmail(basicInfoDTO.getEmail());
            if (isUserRegistered) {
                throw new UserException("User already exists", UserErrorCode.USER_ALREADY_EXISTS);
            }
            String redisKey = "signup:" + basicInfoDTO.getEmail(); // Use email as key
            redisTemplate.opsForHash().put(redisKey, "fullName", basicInfoDTO.getFullName());
            redisTemplate.opsForHash().put(redisKey, "email", basicInfoDTO.getEmail());
            redisTemplate.opsForHash().put(redisKey, "phoneNumber", basicInfoDTO.getPhoneNumber());
            redisTemplate.expire(redisKey, Duration.ofMinutes(30)); //Data expires if incomplete

            log.info("First step successfully completed for {}", basicInfoDTO.getEmail());
            return ApiResponse.success(basicInfoDTO.getEmail(), "Proceed to next step");
        } catch (Exception ex) {
            return handleSignUpException(ex);
        }
    }

    //Step 2: Save password
    public ApiResponse<String> savePassword(String email, PasswordDTO passwordDTO) {
        try {
            String redisKey = "signup:" + email;
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                log.error("Redis key not found for email: {}", email);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid or expired sign up session." +
                        " Please restart the process.");
            }

            redisTemplate.opsForHash().put(redisKey, "password", passwordEncoder.encode(passwordDTO.getPassword()));
            redisTemplate.expire(redisKey, Duration.ofMinutes(30)); //Reset expiration

            log.info("Password successfully saved for {}", email);
            return ApiResponse.success(email, "Proceed to next step. Account validation");
        } catch (Exception ex) {
            return handleSignUpException(ex);
        }
    }

    //Step 3: Capture verification request and validate account to be enabled
    public ApiResponse<String> verifyAccount(VerificationRequest request) {
        log.info("Validation verification code for email {}", request.getEmail());
        try {
            Boolean isVerified = validationClient.verifyCode(request);

            if (isVerified) {

                log.info("Verification successful");
                // Update is_enabled flag for user
                String redisKey = "signup:" + request.getEmail();
                if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                    log.error("Key not found for email: {}", request.getEmail());
                    return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid or expired sign up session." +
                            " Please restart the process.");
                }
                redisTemplate.opsForHash().put(redisKey, "isEnabled", true);
                redisTemplate.expire(redisKey, Duration.ofMinutes(30)); // Reset expiration time

                return ApiResponse.success("Verification successful");

            } else {
                log.error("Failed to validate account.");
                return ApiResponse.success("Verification failed");
            }
        } catch (Exception ex) {
            return handleSignUpException(ex);
        }

    }

    // Step 4: Capture profile info and finalize the process
    public ApiResponse<ResponseTokenDTO> saveProfileInfo(String email, ProfileInfoDTO profileInfoDTO) {
        try {
            String redisKey = "signup:" + email;
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                log.error("Redis Key not found for email: {}", email);
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid or expired sign up session." +
                        " Please restart the process.");
            }

            // Retrieve basic info from Redis
            Map<Object, Object> signupData = redisTemplate.opsForHash().entries(redisKey);
            String fullName = (String) signupData.get("fullName");
            String redisEmail = (String) signupData.get("email");
            String phoneNumber = (String) signupData.get("phoneNumber");
            String encodedPassword = (String) signupData.get("password");
            boolean isEnabled = (Boolean) signupData.get("isEnabled");

            // Build User and save details to database
            User user = User.builder()
                    .fullName(fullName)
                    .email(redisEmail)
                    .phoneNumber(phoneNumber)
                    .password(encodedPassword)
                    .isEnabled(isEnabled)
                    .build();

            // Save time stamp details
            setTimestampDetails(user, OffsetDateTime.now());

            // Build user roles and save. A new user gets ROLE_USER by default
            Role userRole = rolesRepo.findByRoleType(RoleType.USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            user.setRoles(List.of(userRole));

            // Build user's profile
            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .nationality(profileInfoDTO.getNationality())
                    .dateOfBirth(profileInfoDTO.getDateOfBirth())
                    .identificationNumber(profileInfoDTO.getIdentificationNumber())
                    .build();

            // Build user address and save
            Address address = Address.builder()
                    .city(profileInfoDTO.getCity())
                    .state(profileInfoDTO.getState())
                    .street(profileInfoDTO.getStreet())
                    .zipCode(profileInfoDTO.getZipCode())
                    .build();

            userProfile.setAddress(address);

            // Save the data to db and flush redis template
            userRepo.save(user);
            userProfileRepo.save(userProfile);
            redisTemplate.delete(redisKey);

            log.info("Cleared redis cache for email: {}", email);

            ResponseTokenDTO response =
                    new ResponseTokenDTO(user.getEmail(), jwtService.generateToken(user));

            return ApiResponse.success(response, "Sign up completed successfully");
        } catch (Exception e) {
            return handleSignUpException(e);
        }
    }

    private <T> ApiResponse<T> handleSignUpException(Throwable ex) {
        Throwable cause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
        log.error("Error processing request, caused by: {}", cause.getMessage());
        return switch (cause) {
            case UserException userException -> ApiResponse.error(HttpStatus.UNAUTHORIZED, userException.getMessage());
            case ValidationException validationException ->
                    ApiResponse.error(HttpStatus.BAD_REQUEST, validationException.getMessage());
            case TechnicalException technicalException ->
                    ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, technicalException.getMessage());
            default -> ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        };
    }

    // Method to extract time stamp details
    private void setTimestampDetails(User user, OffsetDateTime now) {
        user.setDayOfTheWeek(now.getDayOfWeek().name());
        user.setMonth(now.getMonth().name());
        user.setYear(now.getYear());
        user.setTime(now.toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString());
        user.setDate(now.toLocalDate());
    }

}
