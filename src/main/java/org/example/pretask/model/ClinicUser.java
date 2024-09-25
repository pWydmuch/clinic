package org.example.pretask.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Inheritance
@Table(name = "users")
@DiscriminatorColumn
public class ClinicUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Column(unique = true)
    private Long pesel;
    @Column(unique = true)
    private String login;
    private String password;

}
