package org.chubb.flightbookingapplication.repository;


import org.chubb.flightbookingapplication.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureTimeBetweenAndActiveTrue(
            String fromPlace,
            String toPlace,
            LocalDateTime start,
            LocalDateTime end
    );
}
