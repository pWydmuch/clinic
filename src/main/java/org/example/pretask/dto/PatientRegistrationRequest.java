package org.example.pretask.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientRegistrationRequest(@NotBlank String name,
                                         @NotBlank String surname,
                                         @Min(18) Integer age,
                                         Long pesel,
                                         @NotBlank String login,
                                         @Size(min = 8) String password) {
}