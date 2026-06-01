package com.svalero.transportFleet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverInV2Dto {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    private String surname;

    @NotBlank(message = "License category cannot be blank")
    private String licenseCategory;

    private LocalDate birthDate;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @Min(value = 0, message = "Experience must be positive")
    private int experience;

    private float height;

    private boolean active;
}
