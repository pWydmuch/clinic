package org.example.pretask.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@DiscriminatorValue("patient")
public class Patient extends ClinicUser {
    private Integer age;
    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

}
