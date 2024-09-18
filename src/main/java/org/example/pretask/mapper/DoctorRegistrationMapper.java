package org.example.pretask.mapper;

import org.example.pretask.dto.DoctorRegistrationRequest;
import org.example.pretask.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorRegistrationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    Doctor dtoToEntity(DoctorRegistrationRequest registrationRequest);
}
