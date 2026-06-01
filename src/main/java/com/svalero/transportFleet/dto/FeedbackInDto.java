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
public class FeedbackInDto {
    @Min(value = 1, message = "Rating must be at least 1")
    private float rating;
    @NotBlank(message = "Comment is mandatory")
    private String comment;
    private LocalDate registerDate;
    private boolean visible;
    private int helpfulCount;
    private boolean recommend;
    private long routeId;
    private long userId;
}
