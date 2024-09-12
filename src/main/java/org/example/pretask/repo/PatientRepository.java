package org.example.pretask.repo;

import org.example.pretask.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Boolean existsByLogin(String login);
    Boolean existsByPesel(Long pesel);
}
