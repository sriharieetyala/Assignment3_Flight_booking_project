package org.chubb.flightbookingapplication.model;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Airline airline;

    @Column(nullable = false, length = 20)
    private String flightNumber;

    @Column(nullable = false, length = 50)
    private String fromPlace;

    @Column(nullable = false, length = 50)
    private String toPlace;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal oneWayPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal roundTripPrice;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int availableSeats;

    @Column(nullable = false)
    private boolean active = true;
}
