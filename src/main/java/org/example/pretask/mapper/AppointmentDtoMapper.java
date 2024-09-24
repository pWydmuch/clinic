package org.example.pretask.mapper;

import org.example.pretask.dto.AppointmentDto;
import org.example.pretask.model.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentDtoMapper {

    AppointmentDto toDto(Appointment appointment);
}
