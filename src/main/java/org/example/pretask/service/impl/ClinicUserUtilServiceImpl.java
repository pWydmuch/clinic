package org.example.pretask.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.pretask.exception.UserAlreadyExistException;
import org.example.pretask.model.ClinicUser;
import org.example.pretask.repo.ClinicUserRepository;
import org.example.pretask.service.ClinicUserUtilService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicUserUtilServiceImpl implements ClinicUserUtilService {

    private final ClinicUserRepository<ClinicUser> clinicUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void checkIfUserAlreadyExists(String login, Long pesel) {
        if (clinicUserRepository.existsByLogin(login)) {
            throw new UserAlreadyExistException("Login already taken");
        }
        if (clinicUserRepository.existsByPesel(pesel)) {
            throw new UserAlreadyExistException("User with pesel: " + pesel + " already exists");
        }
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
