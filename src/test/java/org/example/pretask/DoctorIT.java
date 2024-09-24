package org.example.pretask;

import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.model.Appointment;
import org.example.pretask.model.AppointmentStatus;
import org.example.pretask.repo.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

public class DoctorIT extends BaseIT {

    private static final String DOCTORS_PREFIX = "/doctors";

    @Autowired
    private DoctorRepository doctorRepository;

    @Nested
    public class AuthenticatedCases {

        private String token;

        @BeforeEach
        public void authenticate() {
            LoginRequest loginRequest = new LoginRequest("login-doctor", "123456789");
            LoginResponse loginResponse = webTestClient.post()
                    .uri("/login")
                    .bodyValue(loginRequest)
                    .exchange().returnResult(LoginResponse.class).getResponseBody().blockFirst();
            token = "Bearer " + loginResponse.token();
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldCancelAppointmentWhenAppointmentOfGivenDoctor() {
            webTestClient.put()
                    .uri(DOCTORS_PREFIX + "/appointments/1/cancellation")
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
        public void shouldNotCancelAppointmentWhenAppointmentNotOfGivenDoctor() {
            webTestClient.put()
                    .uri(DOCTORS_PREFIX + "/appointments/4/cancellation")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isBadRequest();
            Appointment appointment = appointmentRepository.findById(4L).orElseThrow();
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldReturnBadRequestWhenAppointmentAlreadyCancelled() {
            webTestClient.put()
                    .uri(DOCTORS_PREFIX + "/appointments/3/cancellation")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody().json("""
                            {"message":"Appointment with id: 3 has already been cancelled"}
                            """);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldReturnListOfPatientsOfGivenDoctor() {
            webTestClient.get()
                    .uri(DOCTORS_PREFIX + "/patients")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody().json("""
                            [{"name":"Jan","surname":"Nowak","pesel":88888888888,"age":23},{"name":"Michal","surname":"Nowak","pesel":77777777777,"age":43}]
                            """);
        }

        @Test
        @Sql("/clear-db.sql")
        @Sql("/test-data.sql")
        public void shouldReturnListOfAppointmentsOfPatientsOfGivenDoctor() {
            webTestClient.get()
                    .uri(DOCTORS_PREFIX + "/patients/2/appointments")
                    .header("Authorization", token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody().json("""
                         [{"patient":{"name":"Jan","surname":"Nowak","pesel":88888888888,"age":23},"date":"2016-03-18T13:56:39.492","status":"CANCELLED"},{"patient":{"name":"Jan","surname":"Nowak","pesel":88888888888,"age":23},"date":"2016-03-16T13:56:39.492","status":"SCHEDULED"}]
                         """);
        }
    }

    @Sql("/clear-db.sql")
    @Test
    public void shouldRegisterDoctor() {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest("Jan", "Nowak", 24,
                99999999999L, "cardiologist", "jan-nowak", "12345678");
        webTestClient.post()
                .uri(DOCTORS_PREFIX + "/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
        assertThat(doctorRepository.findAll()).hasSize(1);
    }

    @Test
    @Sql("/clear-db.sql")
    @Sql("/test-data.sql")
    public void shouldNotRegisterDoctorWhenLoginAlreadyTaken() {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest("Jan", "Nowak", 24,
                99999999999L, "cardiologist", "login", "12345678");
        webTestClient.post()
                .uri(DOCTORS_PREFIX + "/registration")
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
    public void shouldNotRegisterDoctorWhenUserWithThisPeselAlreadyExist() {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest("Jan", "Nowak", 24,
                88888888888L, "psychitirst", "another-login", "12345678");
        webTestClient.post()
                .uri(DOCTORS_PREFIX + "/registration")
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
    public void shouldNotRegisterDoctorWhenAgeLowerThan18() {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest("Jan", "Nowak", 17,
                99999999999L, "cardiologist", "jan-nowak", "12345678");
        webTestClient.post()
                .uri(DOCTORS_PREFIX + "/registration")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("""
                        {"message":"age must be greater than or equal to 18"}""");
    }

}
