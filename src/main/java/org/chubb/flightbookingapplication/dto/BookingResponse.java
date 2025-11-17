package org.chubb.flightbookingapplication.dto;



import org.chubb.flightbookingapplication.model.BookingStatus;
import org.chubb.flightbookingapplication.model.MealType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private String pnr;
    private Long flightId;
    private String airlineName;
    private String customerName;
    private String email;
    private int numberOfSeats;
    private MealType mealType;
    private LocalDate journeyDate;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private List<PassengerInfo> passengers;

    @Data
    @Builder
    public static class PassengerInfo {
        private String name;
        private String gender;
        private int age;
        private String seatNumber;
    }
}
