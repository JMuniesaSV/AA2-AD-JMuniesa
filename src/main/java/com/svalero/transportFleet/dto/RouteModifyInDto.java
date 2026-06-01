package com.svalero.transportFleet.dto;

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
public class RouteModifyInDto {
    @NotNull(message = "Name is mandatory")
    private String name;
    private String description;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate routeDate;
    @NotNull(message = "Category is mandatory")
    private String category;
    @Min(value = 1, message = "The capacity must be at least 1")
    private int capacity;
    @Min(value = 0, message = "The price must be a positive number")
    private float price;
    private boolean availability;
    private long stationId;
    private List<Long> driversIds;
}
