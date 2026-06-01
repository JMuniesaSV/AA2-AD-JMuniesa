package com.svalero.transportFleet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverSummaryV2Dto {
    private long id;
    private String fullName;
    private String licenseCategory;
    private boolean active;
}
