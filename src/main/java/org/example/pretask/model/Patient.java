package org.example.pretask.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@DiscriminatorValue("patient")
public class Patient extends ClinicUser {
    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments;
}
