package org.example.pretask.service;

import org.example.pretask.model.ClinicUser;
import org.example.pretask.repo.ClinicUserRepository;
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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final ClinicUserRepository<ClinicUser> clinicUserRepository;

    public JwtUserDetailsService(@Lazy AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, ClinicUserRepository<ClinicUser> clinicUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.clinicUserRepository = clinicUserRepository;
    }

    public String authenticate(String login, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        UserDetails userDetails = loadUserByUsername(login);
        ClinicUser patient = getClinicUser(login);
        return jwtTokenService.generateToken(userDetails, patient.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        ClinicUser clinicUser = getClinicUser(login);
        return new User(clinicUser.getLogin(), clinicUser.getPassword(), new ArrayList<>());
    }

    private ClinicUser getClinicUser(String login) {
        return clinicUserRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
    }

}