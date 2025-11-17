package org.chubb.flightbookingapplication.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FlightSearchResponse {
    private Long flightId;
    private String airlineName;
    private String airlineLogoUrl;
    private String flightNumber;
    private LocalDateTime departureTime;
    private BigDecimal oneWayPrice;
    private BigDecimal roundTripPrice;
}
