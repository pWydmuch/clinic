package org.example.pretask;

import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.repo.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

public class DoctorIT extends BaseIT {

    private static final String DOCTORS_PREFIX = "/doctors";

    @Autowired
    private DoctorRepository doctorRepository;

    @Sql("/clear-db.sql")
    @Test
    public void shouldRegisterDoctor() {
        DoctorRegistrationRequest request = new DoctorRegistrationRequest("Jan", "Nowak", 24,
                99999999999L,"cardiologist" ,"jan-nowak", "12345678");
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
                99999999999L,"cardiologist" ,"login", "12345678");
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
                88888888888L, "psychitirst" ,"another-login", "12345678");
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
                99999999999L,"cardiologist", "jan-nowak", "12345678");
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
