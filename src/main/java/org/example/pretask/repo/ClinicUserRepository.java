package org.example.pretask.repo;

import org.example.pretask.model.ClinicUser;
import org.example.pretask.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicUserRepository<T extends ClinicUser> extends JpaRepository<T, Long> {
    boolean existsByLogin(String login);
    boolean existsByPesel(Long pesel);
    Optional<T> findByLogin(String login);
}
