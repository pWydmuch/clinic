package org.example.pretask.controller;

import jakarta.validation.Valid;
import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid DoctorRegistrationRequest registrationRequest) {
        doctorService.registerNewDoctor(registrationRequest);
    }
}
