package org.chubb.flightbookingapplication.repository;



import org.chubb.flightbookingapplication.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
