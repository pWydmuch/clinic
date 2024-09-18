package org.example.pretask.service;

import org.example.pretask.exception.UserAlreadyExistException;
import org.example.pretask.model.ClinicUser;
import org.example.pretask.repo.ClinicUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClinicUserUtilService {

    private final ClinicUserRepository<ClinicUser> clinicUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ClinicUserUtilService(ClinicUserRepository<ClinicUser> clinicUserRepository, PasswordEncoder passwordEncoder) {
        this.clinicUserRepository = clinicUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void checkIfUserAlreadyExists(String login, Long pesel) {
        if (clinicUserRepository.existsByLogin(login)) {
            throw new UserAlreadyExistException("Login already taken");
        }
        if (clinicUserRepository.existsByPesel(pesel)) {
            throw new UserAlreadyExistException("User with pesel: " + pesel + " already exists");
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
