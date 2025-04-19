package com.capricon.Notifications_Service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    private String email;
    private String code;

    private boolean isUsed;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    private String channel;


}
