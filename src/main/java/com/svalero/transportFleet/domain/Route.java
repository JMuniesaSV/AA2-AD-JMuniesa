package com.svalero.transportFleet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Entity(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "route_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate routeDate;

    @Column
    private String category;

    @Column
    private int capacity;

    @Column
    private float price;

    @Column
    private boolean availability = true;

    @OneToMany(mappedBy = "route", cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "route-feedback")
    private List<Feedback> feedbacks;

    @ManyToOne
    @JoinColumn(name = "station_id")
    @JsonBackReference(value = "station-route")
    private Station station;

    @ManyToMany
    @JoinTable(name = "route_drivers", joinColumns = @JoinColumn(name = "route_id"), inverseJoinColumns = @JoinColumn(name = "driver_id"))
    @JsonIgnoreProperties("routes")
    private List<Driver> drivers;
}
