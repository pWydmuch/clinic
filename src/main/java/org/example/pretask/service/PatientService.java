package org.example.pretask.service;

import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.mapper.PatientRegistrationMapper;
import org.example.pretask.model.Patient;
import org.example.pretask.repo.PatientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientRegistrationMapper patientRegistrationMapper;
    private final ClinicUserUtilService clinicUserUtilService;

    public void registerNewPatient(PatientRegistrationRequest registrationRequest) {
        clinicUserUtilService.checkIfUserAlreadyExists(registrationRequest.login(),registrationRequest.pesel());
        Patient patient = patientRegistrationMapper.dtoToEntity(registrationRequest);
        patient.setPassword(clinicUserUtilService.encodePassword(patient.getPassword()));
        patientRepository.save(patient);
    }

}
