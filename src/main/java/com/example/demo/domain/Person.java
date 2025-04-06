package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate datedenaissance;
    @Enumerated(EnumType.STRING)
    private Status status;

}
