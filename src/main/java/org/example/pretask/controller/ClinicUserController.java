package org.example.pretask.controller;

import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.service.JwtUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClinicUserController {

    private final JwtUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = userDetailsService.authenticate(request.login(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
