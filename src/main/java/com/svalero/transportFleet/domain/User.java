package com.svalero.transportFleet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "telephone_number")
    private int telephoneNumber;

    @Column
    private boolean active = true;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value = "user-feedback")
    private List<Feedback> feedbacks;
}
