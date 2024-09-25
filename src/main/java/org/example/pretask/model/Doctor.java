package org.example.pretask.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@DiscriminatorValue("doctor")
public class Doctor extends ClinicUser {
    private Integer age;
    private String specialization;
    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments;

}
