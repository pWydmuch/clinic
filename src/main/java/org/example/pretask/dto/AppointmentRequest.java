package org.example.pretask.dto;

import java.time.LocalDateTime;

public record AppointmentRequest(Long doctorId, LocalDateTime date) {
}
