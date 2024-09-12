package org.example.pretask.mapper;

import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientRegistrationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    Patient dtoToEntity(PatientRegistrationRequest patientRegistrationRequest);
}
