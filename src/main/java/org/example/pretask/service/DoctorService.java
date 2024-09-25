package org.example.pretask.service;

import org.example.pretask.dto.DoctorRegistrationRequest;

public interface DoctorService {

    void registerNewDoctor(DoctorRegistrationRequest registrationRequest);
}
