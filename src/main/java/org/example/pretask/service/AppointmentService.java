package org.example.pretask.service;

import org.example.pretask.dto.AppointmentDto;
import org.example.pretask.dto.PatientDto;

import java.time.LocalDateTime;
import java.util.Set;

public interface AppointmentService {

    Long createAppointment(Long doctorId, Long patientId, LocalDateTime date);
    void cancelPatientAppointment(Long id, Long patientId);
    void cancelDoctorAppointment(Long id, Long doctorId);
    Set<PatientDto> getPatientsOfDoctor(Long doctorId);
    Set<AppointmentDto> getAppointmentsOfPatientsOfDoctor(Long doctorId, Long patientId);
}
