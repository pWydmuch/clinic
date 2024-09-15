package org.example.pretask.service;

import org.example.pretask.model.Patient;
import org.example.pretask.repo.PatientRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public JwtUserDetailsService(PatientRepository patientRepository, @Lazy AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.patientRepository = patientRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public String authenticate(String login, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        UserDetails userDetails = loadUserByUsername(login);
        Patient patient = getPatient(login);
        return jwtTokenService.generateToken(userDetails, patient.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Patient patient = getPatient(login);
        return new User(patient.getLogin(), patient.getPassword(), new ArrayList<>());
    }

    private Patient getPatient(String login) {
        return patientRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
    }

}