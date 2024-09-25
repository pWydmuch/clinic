package org.example.pretask.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.mapper.DoctorRegistrationMapper;
import org.example.pretask.model.Doctor;
import org.example.pretask.repo.DoctorRepository;
import org.example.pretask.service.ClinicUserUtilService;
import org.example.pretask.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorRegistrationMapper doctorRegistrationMapper;
    private final ClinicUserUtilService clinicUserUtilService;

    @Override
    public void registerNewDoctor(DoctorRegistrationRequest registrationRequest) {
        clinicUserUtilService.checkIfUserAlreadyExists(registrationRequest.login(),registrationRequest.pesel());
        Doctor doctor = doctorRegistrationMapper.dtoToEntity(registrationRequest);
        doctor.setPassword(clinicUserUtilService.encodePassword(doctor.getPassword()));
        doctorRepository.save(doctor);
    }
}
