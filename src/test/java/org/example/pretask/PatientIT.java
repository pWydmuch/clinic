package org.example.pretask;

import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.model.Appointment;
import org.example.pretask.model.AppointmentStatus;
import org.example.pretask.repo.AppointmentRepository;
import org.example.pretask.repo.PatientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PatientIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

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

    @Nested
    public class AuthenticatedCases {

        private String token;

        @BeforeEach
        public void authenticate() {
            LoginRequest loginRequest = new LoginRequest("login", "123456789");
            LoginResponse loginResponse = webTestClient.post()
                    .uri("/login")
                    .bodyValue(loginRequest)
                    .exchange().returnResult(LoginResponse.class).getResponseBody().blockFirst();
            token = "Bearer " + loginResponse.token();
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/create-appointment.sql")
        public void shouldCreateAppointment() {
            AppointmentRequest request = new AppointmentRequest(1L,
                    LocalDateTime.of(LocalDate.of(2022, 12, 12),
                            LocalTime.of(12, 0, 0)));
            Long id = webTestClient.post()
                    .uri("/appointments")
                    .header("Authorization", token)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .isCreated().returnResult(Long.class).getResponseBody().blockFirst();
            assertThat(appointmentRepository.findAll()).hasSize(1);
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldCancelAppointmentWhenAppointmentOfGivenPatient() {
            webTestClient.put()
                    .uri("/appointments/1/cancellation")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isOk();
            Appointment appointment = appointmentRepository.findById(1L).orElseThrow();
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldNotCancelAppointmentWhenAppointmentNotOfGivenPatient() {
            webTestClient.put()
                    .uri("/appointments/2/cancellation")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isBadRequest();
            Appointment appointment = appointmentRepository.findById(2L).orElseThrow();
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldReturnBadRequestWhenAppointmentAlreadyCancelled() {
            webTestClient.put()
                    .uri("/appointments/3/cancellation")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody().json("""
                            {"message":"Appointment with id: 3 has already been cancelled"}
                            """);
        }
    }

    @Sql("/clear-db.sql")
    @Test
    public void shouldRegisterPatient() {
        PatientRegistrationRequest request = new PatientRegistrationRequest("Jan", "Nowak", 24,
                99999999999L, "jan-nowak", "12345678");
        webTestClient.post()
                .uri("/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
        assertThat(patientRepository.findAll()).hasSize(1);
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldNotRegisterPatientWhenLoginAlreadyTaken() {
        PatientRegistrationRequest request = new PatientRegistrationRequest("Jan", "Nowak", 24,
                99999999999L, "login", "12345678");
        webTestClient.post()
                .uri("/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message": "Login already taken"}
                        """);
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldNotRegisterPatientWhenUserWithThisPeselAlreadyExist() {
        PatientRegistrationRequest request = new PatientRegistrationRequest("Jan", "Nowak", 24,
                88888888888L, "another-login", "12345678");
        webTestClient.post()
                .uri("/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message":"User with pesel: 88888888888 already exists"}
                        """);
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldNotRegisterPatientWhenAgeLowerThan18() {
        PatientRegistrationRequest request = new PatientRegistrationRequest("Jan", "Nowak", 17,
                99999999999L, "jan-nowak", "12345678");
        webTestClient.post()
                .uri("/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message":"age must be greater than or equal to 18"}""");
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
