package org.chubb.flightbookingapplication.dto;



import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryAddRequest {

    @NotNull
    private Long airlineId;

    @NotBlank
    private String flightNumber;

    @NotBlank
    private String fromPlace;

    @NotBlank
    private String toPlace;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal oneWayPrice;

    @DecimalMin("0.0")
    private BigDecimal roundTripPrice;

    @Min(1)
    private int totalSeats;
}
