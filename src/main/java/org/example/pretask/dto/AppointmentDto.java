package org.example.pretask.dto;

import org.example.pretask.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentDto(PatientDto patient, LocalDateTime date, AppointmentStatus status) {
}
