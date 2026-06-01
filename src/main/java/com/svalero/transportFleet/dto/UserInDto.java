package com.svalero.transportFleet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInDto {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Surname is mandatory")
    private String surname;
    @NotNull(message = "Birth date is mandatory")
    private LocalDate birthDate;
    private int telephoneNumber;
    private boolean active;
}
