package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverOutDto {
    private long id;
    private String name;
    private String surname;
    private String licenseCategory;
    private LocalDate birthDate;
    private String type;
    private int experience;
    private float height;
    private boolean active;
}
