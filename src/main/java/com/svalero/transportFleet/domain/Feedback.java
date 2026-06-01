package com.svalero.transportFleet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private float rating; // was rate

    @Column
    private String comment;

    @Column(name = "register_date")
    private LocalDate registerDate;

    @Column
    private boolean visible = true;

    @Column
    private int helpfulCount = 0; // was likes

    @Column
    private boolean recommend;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @JsonBackReference(value = "route-feedback")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-feedback")
    private User user;
}
