package org.chubb.flightbookingapplication.repository;



import org.chubb.flightbookingapplication.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
}
