package org.chubb.flightbookingapplication.service;

import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.dto.FlightSearchResponse;
import org.chubb.flightbookingapplication.dto.InventoryAddRequest;

import java.util.List;

public interface FlightService {

    void addInventory(InventoryAddRequest request);

    List<FlightSearchResponse> searchFlights(FlightSearchRequest request);
}
