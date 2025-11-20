package org.chubb.flightbookingapplication.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chubb.flightbookingapplication.controller.FlightController;
import org.chubb.flightbookingapplication.dto.BookingRequest;
import org.chubb.flightbookingapplication.dto.BookingResponse;
import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.dto.InventoryAddRequest;
import org.chubb.flightbookingapplication.model.MealType;
import org.chubb.flightbookingapplication.service.BookingService;
import org.chubb.flightbookingapplication.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchFlights_returnsOk() throws Exception {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace("HYD");
        request.setToPlace("DEL");
        request.setJourneyDate(LocalDate.now().plusDays(1));
        request.setRoundTrip(false);

        Mockito.when(flightService.searchFlights(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/flight/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addInventory_returnsCreated() throws Exception {
        InventoryAddRequest request = new InventoryAddRequest();
        request.setAirlineId(1L);
        request.setFlightNumber("AI-101");
        request.setFromPlace("HYD");
        request.setToPlace("DEL");
        request.setDepartureTime(LocalDateTime.now().plusDays(1));
        request.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        request.setOneWayPrice(BigDecimal.valueOf(5000));
        request.setRoundTripPrice(BigDecimal.valueOf(9000));
        request.setTotalSeats(100);

        Mockito.doNothing().when(flightService).addInventory(any());

        mockMvc.perform(post("/api/flight/airline/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test

    void bookTicket_returnsCreated() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCustomerName("Srihari");
        request.setEmail("srihari@example.com");
        request.setNumberOfSeats(1);
        request.setMealType(MealType.VEG);  // REQUIRED
        request.setJourneyDate(LocalDate.now().plusDays(1));

        BookingRequest.PassengerRequest p = new BookingRequest.PassengerRequest();
        p.setName("Passenger 1");
        p.setGender("M");
        p.setAge(25);
        p.setSeatNumber("1A");

        request.setPassengers(List.of(p)); // REQUIRED

        BookingResponse response = BookingResponse.builder()
                .pnr("PNR12345")
                .customerName("Srihari")
                .email("srihari@example.com")
                .numberOfSeats(1)
                .build();

        Mockito.when(bookingService.bookTicket(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/flight/booking/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }


    @Test
    void getTicket_returnsOk() throws Exception {
        BookingResponse response = BookingResponse.builder()
                .pnr("PNR1")
                .customerName("Srihari")
                .build();

        Mockito.when(bookingService.getTicketByPnr("PNR1"))
                .thenReturn(response);

        mockMvc.perform(get("/api/flight/ticket/PNR1"))
                .andExpect(status().isOk());
    }

    @Test
    void cancelBooking_returnsOk() throws Exception {
        Mockito.doNothing().when(bookingService).cancelBooking("PNR1");

        mockMvc.perform(delete("/api/flight/booking/cancel/PNR1"))
                .andExpect(status().isOk());
    }
}
