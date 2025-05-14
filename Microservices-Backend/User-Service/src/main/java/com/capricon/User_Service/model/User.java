package com.capricon.User_Service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEnabled = false; //Returns true if account is enabled

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCredentialsNonExpired = true; //Returns true if credentials are valid (not yet expired)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccountNonLocked = true; //Returns true if the account is not locked

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccountNonExpired = true; //Returns true if account is not expired

    @Column(nullable = false)
    private String dayOfTheWeek;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private LocalDate date;

}
