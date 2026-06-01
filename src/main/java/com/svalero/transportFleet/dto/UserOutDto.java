package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOutDto {
    private long id;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private int telephoneNumber;
    private boolean active;
}
