package org.chubb.flightbookingapplication.controller;

import org.chubb.flightbookingapplication.dto.*;
import org.chubb.flightbookingapplication.service.BookingService;
import org.chubb.flightbookingapplication.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<String> addInventory(@Valid @RequestBody InventoryAddRequest request) {
        flightService.addInventory(request);
        return ResponseEntity.ok("Inventory added successfully");
    }

    @PostMapping("/search")
    public List<FlightSearchResponse> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }

    @PostMapping("/booking/{flightId}")
    public BookingResponse bookTicket(@PathVariable Long flightId,
                                      @Valid @RequestBody BookingRequest request) {
        request.setFlightId(flightId);
        return bookingService.bookTicket(request);
    }

    @GetMapping("/ticket/{pnr}")
    public BookingResponse getTicket(@PathVariable String pnr) {
        return bookingService.getTicketByPnr(pnr);
    }

    @GetMapping("/booking/history/{emailId}")
    public List<BookingResponse> getHistory(@PathVariable("emailId") String emailId) {
        return bookingService.getBookingHistory(emailId);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<String> cancel(@PathVariable String pnr) {
        bookingService.cancelBooking(pnr);
        return ResponseEntity.ok("Booking cancelled (if allowed)");
    }
}
