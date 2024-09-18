package org.example.pretask;

import org.example.pretask.dto.LoginRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class BaseIT {

    @Autowired
    protected WebTestClient webTestClient;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine")
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldLoginPatientWhenUserExists() {
        LoginRequest loginRequest = new LoginRequest("login", "123456789");
        webTestClient.post()
                .uri("/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().jsonPath("$.token").isNotEmpty();
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldNotLoginPatientWhenUserNotExists() {
        LoginRequest loginRequest = new LoginRequest("no_user_login", "123456789");
        webTestClient.post()
                .uri("/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("jwt.secret", () -> "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9");
    }

}
