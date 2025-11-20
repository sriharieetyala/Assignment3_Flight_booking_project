package org.chubb.flightbookingapplication.service.impl;

import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.dto.FlightSearchResponse;
import org.chubb.flightbookingapplication.dto.InventoryAddRequest;
import org.chubb.flightbookingapplication.model.Airline;
import org.chubb.flightbookingapplication.model.Flight;
import org.chubb.flightbookingapplication.repository.AirlineRepository;
import org.chubb.flightbookingapplication.repository.FlightRepository;
import org.chubb.flightbookingapplication.service.FlightService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    public FlightServiceImpl(FlightRepository flightRepository,
                             AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
    }

    // ---------- ADD INVENTORY ----------
    @Override
    @Transactional
    public void addInventory(InventoryAddRequest request) {

        // 1) Arrival must be after departure
        if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        // 2) Duplicate flight check
        boolean exists = flightRepository.existsByAirlineIdAndFlightNumberAndDepartureTime(
                request.getAirlineId(),
                request.getFlightNumber(),
                request.getDepartureTime()
        );
        if (exists) {
            throw new IllegalStateException("Flight already exists for this airline and departure time");
        }

        // 3) Airline must exist
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

        // 4) Save flight
        Flight flight = Flight.builder()
                .airline(airline)
                .flightNumber(request.getFlightNumber())
                .fromPlace(request.getFromPlace())
                .toPlace(request.getToPlace())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .oneWayPrice(request.getOneWayPrice())
                .roundTripPrice(request.getRoundTripPrice())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .active(true)
                .build();

        flightRepository.save(flight);
    }

    // ---------- SEARCH FLIGHTS ----------
    @Override
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest request) {

        LocalDateTime start = request.getJourneyDate().atStartOfDay();
        LocalDateTime end = request.getJourneyDate().atTime(LocalTime.MAX);

        return flightRepository
                .findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureTimeBetweenAndActiveTrue(
                        request.getFromPlace(),
                        request.getToPlace(),
                        start,
                        end
                ).stream()
                .map(f -> FlightSearchResponse.builder()
                        .flightId(f.getId())
                        .airlineName(f.getAirline().getName())
                        .airlineLogoUrl(f.getAirline().getLogoUrl())
                        .flightNumber(f.getFlightNumber())
                        .departureTime(f.getDepartureTime())
                        .oneWayPrice(f.getOneWayPrice())
                        .roundTripPrice(f.getRoundTripPrice())
                        .build()
                )
                .toList();
    }
}
