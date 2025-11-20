package org.chubb.flightbookingapplication.dto;



import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

public class InventoryAddRequest {

    @NotNull(message = "airlineId is required")
    private Long airlineId;

    @NotBlank(message = "flightNumber is required")
    private String flightNumber;

    @NotBlank(message = "fromPlace is required")
    private String fromPlace;

    @NotBlank(message = "toPlace is required")
    private String toPlace;

    @NotNull(message = "departureTime is required")
    private LocalDateTime departureTime;

    @NotNull(message = "arrivalTime is required")
    private LocalDateTime arrivalTime;

    @NotNull(message = "oneWayPrice is required")
    @DecimalMin(value = "0.01", message = "oneWayPrice must be greater than zero")
    private BigDecimal oneWayPrice;

    @NotNull(message = "roundTripPrice is required")
    @DecimalMin(value = "0.01", message = "roundTripPrice must be greater than zero")
    private BigDecimal roundTripPrice;

    @Min(value = 1, message = "totalSeats must be at least 1")
    private int totalSeats;


}
