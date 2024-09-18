package org.example.pretask.controller;

import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.service.JwtUserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClinicUserController {

    private final JwtUserDetailsService userDetailsService;

    public ClinicUserController(JwtUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userDetailsService.authenticate(request.login(), request.password());
        return new LoginResponse(token);
    }

}
