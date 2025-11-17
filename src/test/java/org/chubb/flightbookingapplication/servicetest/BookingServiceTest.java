package org.chubb.flightbookingapplication.servicetest;


import org.chubb.flightbookingapplication.dto.BookingRequest;
import org.chubb.flightbookingapplication.dto.BookingRequest.PassengerRequest;
import org.chubb.flightbookingapplication.model.*;
import org.chubb.flightbookingapplication.repository.BookingRepository;
import org.chubb.flightbookingapplication.repository.FlightRepository;
import org.chubb.flightbookingapplication.repository.PassengerRepository;
import org.chubb.flightbookingapplication.service.impl.BookingServiceImpl;
import org.chubb.flightbookingapplication.service.util.PnrGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PnrGenerator pnrGenerator;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Flight flight;

    @BeforeEach
    void setUp() {
        Airline airline = Airline.builder()
                .id(1L)
                .code("AI")
                .name("Air India")
                .build();

        flight = Flight.builder()
                .id(1L)
                .airline(airline)
                .flightNumber("AI-101")
                .fromPlace("HYD")
                .toPlace("DEL")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .oneWayPrice(BigDecimal.valueOf(5000))
                .roundTripPrice(BigDecimal.valueOf(9000))
                .totalSeats(100)
                .availableSeats(50)
                .active(true)
                .build();
    }

    @Test
    void bookTicket_success() {
        BookingRequest request = new BookingRequest();
        request.setFlightId(1L);
        request.setCustomerName("Srihari");
        request.setEmail("test@example.com");
        request.setNumberOfSeats(2);
        request.setMealType(MealType.VEG);
        request.setJourneyDate(LocalDate.now().plusDays(2));

        PassengerRequest p1 = new PassengerRequest();
        p1.setName("P1");
        p1.setGender("M");
        p1.setAge(25);
        p1.setSeatNumber("1A");

        PassengerRequest p2 = new PassengerRequest();
        p2.setName("P2");
        p2.setGender("F");
        p2.setAge(24);
        p2.setSeatNumber("1B");

        request.setPassengers(List.of(p1, p2));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(pnrGenerator.generate()).thenReturn("ABC12345");
        when(bookingRepository.save(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = bookingService.bookTicket(request);

        assertEquals("ABC12345", response.getPnr());
        assertEquals(2, response.getNumberOfSeats());
        verify(bookingRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void cancelBookingTooLate_throwsException() {

        var booking = Booking.builder()
                .pnr("PNR1")
                .flight(Flight.builder()
                        .departureTime(LocalDateTime.now().plusHours(5))
                        .build())
                .status(BookingStatus.CONFIRMED)
                .numberOfSeats(1)
                .build();

        when(bookingRepository.findByPnr("PNR1"))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class,
                () -> bookingService.cancelBooking("PNR1"));
    }
}
