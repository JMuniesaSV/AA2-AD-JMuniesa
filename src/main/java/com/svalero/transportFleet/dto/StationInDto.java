package com.svalero.transportFleet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationInDto {
    @NotNull(message = "Name is mandatory")
    private String name;
    private String description;
    @NotNull(message = "Category is mandatory")
    private String category;
    private String streetLocated;
    @Min(10000)
    @Max(99999)
    private int postalCode;
    private LocalDate registerDate;
    private boolean disabledAccess;
    private double longitude;
    private double latitude;
}
