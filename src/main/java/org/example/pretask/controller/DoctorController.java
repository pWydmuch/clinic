package org.example.pretask.controller;

import jakarta.validation.Valid;
import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.service.AppointmentService;
import org.example.pretask.service.DoctorService;
import org.example.pretask.service.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final JwtTokenService jwtTokenService;

    public DoctorController(AppointmentService appointmentService, DoctorService doctorService, JwtTokenService jwtTokenService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid DoctorRegistrationRequest registrationRequest) {
        doctorService.registerNewDoctor(registrationRequest);
    }

    @PutMapping("/appointments/{appointmentId}/cancellation")
    public void cancelAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String authorizationHeader) {
        Long doctorId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        appointmentService.cancelDoctorAppointment(appointmentId, doctorId);
    }
}
