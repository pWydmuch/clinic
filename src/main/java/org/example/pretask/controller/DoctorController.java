package org.example.pretask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.AppointmentDto;
import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.dto.PatientDto;
import org.example.pretask.service.AppointmentService;
import org.example.pretask.service.DoctorService;
import org.example.pretask.service.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@RequestBody @Valid DoctorRegistrationRequest registrationRequest) {
        doctorService.registerNewDoctor(registrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/appointments/{appointmentId}/cancellation")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String authorizationHeader) {
        Long doctorId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        appointmentService.cancelDoctorAppointment(appointmentId, doctorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/patients")
    public ResponseEntity<Set<PatientDto>> getPatientsWithAppointmentsWithDoctor(@RequestHeader("Authorization") String authorizationHeader) {
        Long doctorId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(appointmentService.getPatientsOfDoctor(doctorId));
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<Set<AppointmentDto>> getAppointmentsOfGivenPatient(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long patientId) {
        Long doctorId = jwtTokenService.getIdFromToken(authorizationHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(appointmentService.getAppointmentsOfPatientsOfDoctor(doctorId, patientId));
    }
}
