package org.chubb.flightbookingapplication.service;



import org.chubb.flightbookingapplication.dto.FlightSearchRequest;
import org.chubb.flightbookingapplication.dto.FlightSearchResponse;
import org.chubb.flightbookingapplication.dto.InventoryAddRequest;
import org.chubb.flightbookingapplication.model.Flight;

import java.util.List;

public interface FlightService {

    Flight addInventory(InventoryAddRequest request);

    List<FlightSearchResponse> searchFlights(FlightSearchRequest request);
}
