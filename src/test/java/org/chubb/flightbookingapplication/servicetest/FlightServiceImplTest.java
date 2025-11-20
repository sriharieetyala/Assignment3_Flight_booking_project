package org.chubb.flightbookingapplication.servicetest;

import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.dto.FlightSearchResponse;
import org.chubb.flightbookingapplication.dto.InventoryAddRequest;
import org.chubb.flightbookingapplication.model.Airline;
import org.chubb.flightbookingapplication.model.Flight;
import org.chubb.flightbookingapplication.repository.AirlineRepository;
import org.chubb.flightbookingapplication.repository.FlightRepository;
import org.chubb.flightbookingapplication.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private InventoryAddRequest validRequest;
    private Airline airline;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id(1L)
                .code("AI")
                .name("Air India")
                .build();

        validRequest = new InventoryAddRequest();
        validRequest.setAirlineId(1L);
        validRequest.setFlightNumber("AI-101");
        validRequest.setFromPlace("HYD");
        validRequest.setToPlace("DEL");
        validRequest.setDepartureTime(LocalDateTime.now().plusDays(1));
        validRequest.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        validRequest.setOneWayPrice(BigDecimal.valueOf(5000));
        validRequest.setRoundTripPrice(BigDecimal.valueOf(9000));
        validRequest.setTotalSeats(100);
    }

    @Test
    void addInventory_success_savesFlight() {
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(flightRepository.existsByAirlineIdAndFlightNumberAndDepartureTime(
                anyLong(), anyString(), any(LocalDateTime.class)))
                .thenReturn(false);

        flightService.addInventory(validRequest);

        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void addInventory_arrivalBeforeDeparture_throwsIllegalArgumentException() {
        InventoryAddRequest bad = new InventoryAddRequest();
        bad.setAirlineId(1L);
        bad.setFlightNumber("AI-102");
        bad.setFromPlace("HYD");
        bad.setToPlace("DEL");
        bad.setDepartureTime(LocalDateTime.now().plusDays(1));
        bad.setArrivalTime(LocalDateTime.now().plusDays(1).minusHours(1)); // before departure
        bad.setOneWayPrice(BigDecimal.valueOf(5000));
        bad.setRoundTripPrice(BigDecimal.valueOf(9000));
        bad.setTotalSeats(100);

        assertThrows(IllegalArgumentException.class,
                () -> flightService.addInventory(bad));

        verify(flightRepository, never()).save(any());
    }

    @Test
    void addInventory_duplicateFlight_throwsIllegalStateException() {
        when(flightRepository.existsByAirlineIdAndFlightNumberAndDepartureTime(
                anyLong(), anyString(), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> flightService.addInventory(validRequest));

        verify(flightRepository, never()).save(any());
    }

    @Test
    void searchFlights_returnsMappedResponses() {
        Flight flight = Flight.builder()
                .id(10L)
                .airline(airline)
                .flightNumber("AI-201")
                .fromPlace("HYD")
                .toPlace("DEL")
                .departureTime(LocalDateTime.of(2025, 12, 1, 10, 0))
                .arrivalTime(LocalDateTime.of(2025, 12, 1, 12, 0))
                .oneWayPrice(BigDecimal.valueOf(6000))
                .roundTripPrice(BigDecimal.valueOf(11000))
                .totalSeats(100)
                .availableSeats(80)
                .active(true)
                .build();

        when(flightRepository.findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureTimeBetweenAndActiveTrue(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(flight));

        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace("HYD");
        request.setToPlace("DEL");
        request.setJourneyDate(LocalDate.of(2025, 12, 1));
        request.setRoundTrip(false);

        List<FlightSearchResponse> result = flightService.searchFlights(request);

        assertEquals(1, result.size());
        FlightSearchResponse resp = result.get(0);
        assertEquals(10L, resp.getFlightId());
        assertEquals("Air India", resp.getAirlineName());
        assertEquals("AI-201", resp.getFlightNumber());
    }
}

