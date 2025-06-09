package org.example.pretask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.service.AppointmentService;
import org.example.pretask.service.JwtTokenService;
import org.example.pretask.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final JwtTokenService jwtTokenService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/appointments")
    public ResponseEntity<Long> addAppointment(@RequestBody AppointmentRequest request, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        Long appointmentId = appointmentService.createAppointment(request.doctorId(), patientId, request.date());
        return new ResponseEntity<>(appointmentId, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/appointments/{appointmentId}/cancellation")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String authorizationHeader) {
        Long patientId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        appointmentService.cancelPatientAppointment(appointmentId, patientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@RequestBody @Valid PatientRegistrationRequest registrationRequest) {
        patientService.registerNewPatient(registrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/roletest")
    public ResponseEntity<String> test(@RequestHeader("Authorization") String authorizationHeader) {
        Long doctorId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        return ResponseEntity.ok("granted access " + doctorId);
    }

}
