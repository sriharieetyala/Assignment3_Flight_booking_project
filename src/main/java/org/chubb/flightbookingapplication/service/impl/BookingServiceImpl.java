package org.chubb.flightbookingapplication.service.impl;

import org.chubb.flightbookingapplication.dto.BookingRequest;
import org.chubb.flightbookingapplication.dto.BookingResponse;
import org.chubb.flightbookingapplication.model.*;
import org.chubb.flightbookingapplication.repository.BookingRepository;
import org.chubb.flightbookingapplication.repository.FlightRepository;
import org.chubb.flightbookingapplication.repository.PassengerRepository;
import org.chubb.flightbookingapplication.service.BookingService;
import org.chubb.flightbookingapplication.service.util.PnrGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final PnrGenerator pnrGenerator;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              FlightRepository flightRepository,
                              PassengerRepository passengerRepository,
                              PnrGenerator pnrGenerator) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.pnrGenerator = pnrGenerator;
    }

    @Override
    @Transactional
    public BookingResponse bookTicket(BookingRequest request) {
        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new IllegalStateException("Flight not found"));

        if (request.getNumberOfSeats() > flight.getAvailableSeats()) {
            throw new IllegalStateException("Not enough available seats");
        }

        String pnr = pnrGenerator.generate();

        Booking booking = Booking.builder()
                .pnr(pnr)
                .flight(flight)
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .numberOfSeats(request.getNumberOfSeats())
                .mealType(request.getMealType())
                .journeyDate(request.getJourneyDate())
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();

        // save once and keep reference in a final/effectively-final variable
        Booking savedBooking = bookingRepository.save(booking);

        // normal for-each avoids lambda capture issue
        for (BookingRequest.PassengerRequest pReq : request.getPassengers()) {
            Passenger passenger = Passenger.builder()
                    .booking(savedBooking)
                    .name(pReq.getName())
                    .gender(pReq.getGender())
                    .age(pReq.getAge())
                    .seatNumber(pReq.getSeatNumber())
                    .build();
            passengerRepository.save(passenger);
            savedBooking.getPassengers().add(passenger);
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumberOfSeats());
        flightRepository.save(flight);

        return toResponse(savedBooking);
    }

    @Override
    public BookingResponse getTicketByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new IllegalStateException("Booking not found"));
        return toResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingHistory(String email) {
        return bookingRepository.findByEmailOrderByBookingTimeDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cancelBooking(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new IllegalStateException("Booking not found"));

        LocalDateTime departure = booking.getFlight().getDepartureTime();
        Duration diff = Duration.between(LocalDateTime.now(), departure);

        if (diff.toHours() < 24) {
            throw new IllegalStateException("Cancellation allowed only 24 hours before departure");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfSeats());
        flightRepository.save(flight);
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .pnr(booking.getPnr())
                .flightId(booking.getFlight().getId())
                .airlineName(booking.getFlight().getAirline().getName())
                .customerName(booking.getCustomerName())
                .email(booking.getEmail())
                .numberOfSeats(booking.getNumberOfSeats())
                .mealType(booking.getMealType())
                .journeyDate(booking.getJourneyDate())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .passengers(
                        booking.getPassengers().stream()
                                .map(p -> BookingResponse.PassengerInfo.builder()
                                        .name(p.getName())
                                        .gender(p.getGender())
                                        .age(p.getAge())
                                        .seatNumber(p.getSeatNumber())
                                        .build())
                                .toList()
                )
                .build();
    }
}
