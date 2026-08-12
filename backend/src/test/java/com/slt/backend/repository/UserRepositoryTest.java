package com.slt.backend.repository;

import com.slt.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .address("123 Main St")
                .build();
        
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnTrueIfExistsByEmail() {
        User user = User.builder()
                .name("Test User")
                .email("exists@example.com")
                .password("password")
                .build();
        
        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmail("exists@example.com");
        assertThat(exists).isTrue();
    }
}
