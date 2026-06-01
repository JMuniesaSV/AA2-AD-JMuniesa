package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteOutDto {
    private long id;
    private String name;
    private String description;
    private LocalDate routeDate;
    private String category;
    private int capacity;
    private float price;
    private boolean availability;
}
