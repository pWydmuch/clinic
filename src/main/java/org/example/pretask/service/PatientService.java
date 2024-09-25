package org.example.pretask.service;

import org.example.pretask.dto.PatientRegistrationRequest;

public interface PatientService {

    void registerNewPatient(PatientRegistrationRequest registrationRequest);
}
