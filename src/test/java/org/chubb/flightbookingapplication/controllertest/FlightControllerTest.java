package org.chubb.flightbookingapplication.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chubb.flightbookingapplication.controller.FlightController;
import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.service.BookingService;
import org.chubb.flightbookingapplication.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(post("/api/v1.0/flight/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
