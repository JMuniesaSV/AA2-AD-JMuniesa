package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackModifyInDto {
    private float rating;
    private String comment;
    private LocalDate registerDate;
    private boolean visible;
    private int helpfulCount;
    private boolean recommend;
    private long routeId;
    private long userId;
}
