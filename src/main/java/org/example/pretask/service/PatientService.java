package org.example.pretask.service;

import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.exception.UserAlreadyExistException;
import org.example.pretask.mapper.PatientRegistrationMapper;
import org.example.pretask.model.Patient;
import org.example.pretask.repo.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRegistrationMapper patientRegistrationMapper;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, PatientRegistrationMapper patientRegistrationMapper) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientRegistrationMapper = patientRegistrationMapper;
    }

    public void registerNewPatient(PatientRegistrationRequest registrationRequest) {
        if (patientRepository.existsByLogin(registrationRequest.login())) {
            throw new UserAlreadyExistException("Login already taken");
        }
        Long pesel = registrationRequest.pesel();
        if (patientRepository.existsByPesel(pesel)) {
            throw new UserAlreadyExistException("User with pesel: " + pesel + " already exists");
        }
        Patient patient = patientRegistrationMapper.dtoToEntity(registrationRequest);
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientRepository.save(patient);
    }
}
