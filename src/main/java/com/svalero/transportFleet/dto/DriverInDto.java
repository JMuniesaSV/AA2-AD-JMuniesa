package com.svalero.transportFleet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverInDto {
    @NotNull(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Surname is mandatory")
    private String surname;
    @NotNull(message = "License category is mandatory")
    private String licenseCategory;
    private LocalDate birthDate;
    @NotNull(message = "Type is mandatory")
    private String type;
    @Min(value = 0, message = "Experience must be a positive number")
    private int experience;
    private float height;
    private boolean active;
}
