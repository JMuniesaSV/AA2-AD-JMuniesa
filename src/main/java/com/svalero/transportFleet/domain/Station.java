package com.svalero.transportFleet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "Name is mandatory")
    private String name;

    @Column
    private String description;

    @Column
    @NotNull(message = "Category is mandatory")
    private String category;

    @Column(name = "street_located")
    private String streetLocated;

    @Column(name = "postal_code")
    @Min(50001)
    @Max(50019)
    private int postalCode;

    @Column(name = "register_date")
    private LocalDate registerDate;

    @Column(name = "disabled_access")
    private boolean disabledAccess;

    @Column
    private double longitude;

    @Column
    private double latitude;

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "station-route")
    private List<Route> routes;
}
