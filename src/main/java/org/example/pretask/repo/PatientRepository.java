package org.example.pretask.repo;

import org.example.pretask.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByLogin(String login);
    boolean existsByPesel(Long pesel);
    Optional<Patient> findByLogin(String login);
}
