package org.example.pretask;

import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.model.Appointment;
import org.example.pretask.model.AppointmentStatus;
import org.example.pretask.repo.AppointmentRepository;
import org.example.pretask.repo.PatientRepository;
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

    @Test
    @Sql("/create-appointment.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCreateAppointment() {
        AppointmentRequest request = new AppointmentRequest(1L,
                LocalDateTime.of(LocalDate.of(2022, 12, 12),
                        LocalTime.of(12, 0, 0)));
        webTestClient.post()
                .uri("/appointments")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
        assertThat(appointmentRepository.findAll()).hasSize(1);
    }

    @Test
    @Sql("/cancel-appointment.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCancelAppointmentWhenAppointmentOfGivenPatient() {
        webTestClient.put()
                .uri("/appointments/1/cancellation")
                .exchange()
                .expectStatus()
                .isOk();
        Appointment appointment = appointmentRepository.findById(1L).orElseThrow();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @Sql("/cancel-appointment.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldNotCancelAppointmentWhenAppointmentNotOfGivenPatient() {
        webTestClient.put()
                .uri("/appointments/2/cancellation")
                .exchange()
                .expectStatus()
                .isBadRequest();
        Appointment appointment = appointmentRepository.findById(2L).orElseThrow();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @Sql("/cancel-appointment.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnBadRequestWhenAppointmentAlreadyCancelled() {
        webTestClient.put()
                .uri("/appointments/3/cancellation")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message":"Appointment with id: 3 has already been cancelled"}
                        """);
    }

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
    @Sql("/user-already-exists.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Sql("/user-already-exists.sql")
    @Sql(scripts = "/clear-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
