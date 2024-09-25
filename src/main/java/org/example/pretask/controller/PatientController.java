package org.example.pretask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.service.AppointmentService;
import org.example.pretask.service.JwtTokenService;
import org.example.pretask.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public Long addAppointment(@RequestBody AppointmentRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        return appointmentService.createAppointment(request.doctorId(), patientId, request.date());
    }

    @PutMapping("/appointments/{appointmentId}/cancellation")
    public void cancelAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        appointmentService.cancelPatientAppointment(appointmentId, patientId);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid PatientRegistrationRequest registrationRequest) {
        patientService.registerNewPatient(registrationRequest);
    }

}
