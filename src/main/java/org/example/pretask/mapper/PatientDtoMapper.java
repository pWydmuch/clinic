package org.example.pretask.mapper;

import org.example.pretask.dto.PatientDto;
import org.example.pretask.dto.PatientRegistrationRequest;
import org.example.pretask.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientDtoMapper {
    PatientDto entityToDto(Patient patient);
}
