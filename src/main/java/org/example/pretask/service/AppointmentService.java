package org.example.pretask.service;

import lombok.RequiredArgsConstructor;
import org.example.pretask.dto.AppointmentDto;
import org.example.pretask.dto.PatientDto;
import org.example.pretask.exception.AppointmentAlreadyCancelledException;
import org.example.pretask.mapper.AppointmentDtoMapper;
import org.example.pretask.mapper.PatientDtoMapper;
import org.example.pretask.model.Appointment;
import org.example.pretask.model.AppointmentStatus;
import org.example.pretask.model.Doctor;
import org.example.pretask.model.Patient;
import org.example.pretask.repo.AppointmentRepository;
import org.example.pretask.repo.DoctorRepository;
import org.example.pretask.repo.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientDtoMapper patientDtoMapper;
    private final AppointmentDtoMapper appointmentDtoMapper;

    public Long createAppointment(Long doctorId, Long patientId, LocalDateTime date) {
        Appointment appointment = new Appointment();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(date);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment).getId();
    }

    public void cancelPatientAppointment(Long id, Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        cancelAppointment(patient.getAppointments(), id);
    }

    public void cancelDoctorAppointment(Long id, Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        cancelAppointment(doctor.getAppointments(), id);
    }

    private void cancelAppointment(Set<Appointment> appointments, Long id) {
        Appointment appointment = appointments
                .stream()
                .filter(x -> x.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("This user doesn't have an appointment with id: " + id));
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentAlreadyCancelledException("Appointment with id: " + id + " has already been cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public Set<PatientDto> getPatientsOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        return doctor.getAppointments()
                .stream()
                .map(Appointment::getPatient)
                .map(patientDtoMapper::entityToDto)
                .collect(Collectors.toSet());
    }

    public Set<AppointmentDto> getAppointmentsOfPatientsOfDoctor(Long doctorId, Long patientId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow()
                .getAppointments()
                .stream()
                .filter(a -> a.getPatient().getId().equals(patientId))
                .map(appointmentDtoMapper::toDto)
                .collect(Collectors.toSet());
    }
}
