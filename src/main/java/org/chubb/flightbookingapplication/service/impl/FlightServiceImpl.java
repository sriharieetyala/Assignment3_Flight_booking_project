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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    public FlightServiceImpl(FlightRepository flightRepository, AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
    }

    @Override
    public Flight addInventory(InventoryAddRequest request) {
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new IllegalStateException("Airline not found"));

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

        return flightRepository.save(flight);
    }

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
