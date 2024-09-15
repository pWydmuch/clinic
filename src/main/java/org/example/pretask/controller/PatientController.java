package org.example.pretask.controller;

import jakarta.validation.Valid;
import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.LoginRequest;
import org.example.pretask.dto.LoginResponse;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.service.AppointmentService;
import org.example.pretask.service.JwtTokenService;
import org.example.pretask.service.JwtUserDetailsService;
import org.example.pretask.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;

    public PatientController(AppointmentService appointmentService,
                             PatientService patientService,
                             JwtUserDetailsService userDetailsService, JwtTokenService jwtTokenService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public Long addAppointment(@RequestBody AppointmentRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        return appointmentService.createAppointment(request.doctorId(), patientId, request.date());
    }

    @PutMapping("/appointments/{appointmentId}/cancellation")
    public void cancelAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        appointmentService.cancelAppointment(appointmentId, patientId);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid PatientRegistrationRequest registrationRequest) {
        patientService.registerNewPatient(registrationRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userDetailsService.authenticate(request.login(), request.password());
        return new LoginResponse(token);
    }
}
