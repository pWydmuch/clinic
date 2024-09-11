package org.example.pretask.controller;

import org.example.pretask.dto.AppointmentRequest;
import org.example.pretask.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    private final AppointmentService appointmentService;

    public PatientController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAppointment(@RequestBody AppointmentRequest request) {
        appointmentService.createAppointment(request.doctorId(), 1L, request.date());
    }
}
