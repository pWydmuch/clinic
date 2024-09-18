package org.example.pretask;

import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.model.Appointment;
import org.example.pretask.model.AppointmentStatus;
import org.example.pretask.repo.AppointmentRepository;
import org.example.pretask.repo.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientIT extends BaseIT {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    private static final String PATIENTS_PREFIX = "/patients";

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
                    .uri(PATIENTS_PREFIX + "/appointments")
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
                    .uri(PATIENTS_PREFIX + "/appointments/1/cancellation")
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
                    .uri(PATIENTS_PREFIX + "/appointments/2/cancellation")
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
                    .uri(PATIENTS_PREFIX + "/appointments/3/cancellation")
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
                .uri(PATIENTS_PREFIX + "/registration")
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
                .uri(PATIENTS_PREFIX + "/registration")
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
                .uri(PATIENTS_PREFIX + "/registration")
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
                .uri(PATIENTS_PREFIX + "/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message":"age must be greater than or equal to 18"}""");
    }

}
