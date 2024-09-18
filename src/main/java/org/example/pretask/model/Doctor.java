package org.example.pretask.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@DiscriminatorValue("doctor")
public class Doctor extends ClinicUser {
    private Integer age;
    private String specialization;
    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
