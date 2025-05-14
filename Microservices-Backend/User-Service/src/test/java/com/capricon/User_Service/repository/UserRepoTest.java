package com.capricon.User_Service.repository;

import com.capricon.User_Service.model.User;
import com.capricon.User_Service.repository.jpa.UserRepo;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@FlywayTest // Runs flyway migrations before each test method
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void shouldFindUserByEmail() {
        //Given (Given a user)
        UUID uuid = UUID.randomUUID();
        User testUser = User.builder()
                .userId(uuid)
                .email("test@example.com")
                .password("password")
                .build();
        entityManager.persist(testUser);
        entityManager.flush();

        // When (When we search for the user by email)
        Optional<User> user = userRepo.findByEmail("test@example.com");

        // Then (Then we should find the user)
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(testUser.getEmail());
    }

}
