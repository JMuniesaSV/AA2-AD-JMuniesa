package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationOutDto {
    private long id;
    private String name;
    private String description;
    private String category;
    private String streetLocated;
    private int postalCode;
    private LocalDate registerDate;
    private boolean disabledAccess;
    private double longitude;
    private double latitude;
}
