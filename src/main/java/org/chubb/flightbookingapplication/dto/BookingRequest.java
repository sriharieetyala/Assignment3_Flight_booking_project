package org.chubb.flightbookingapplication.dto;

import org.chubb.flightbookingapplication.model.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequest {

    // flightId will be set from the path variable in the controller
    // so we do NOT validate it here
    private Long flightId;

    @NotBlank
    private String customerName;

    @Email
    @NotBlank
    private String email;

    @Min(1)
    private int numberOfSeats;

    @NotNull
    private MealType mealType;

    @NotNull
    @FutureOrPresent
    private LocalDate journeyDate;

    @NotEmpty
    @Valid
    private List<PassengerRequest> passengers;

    @Data
    public static class PassengerRequest {
        @NotBlank
        private String name;

        @NotBlank
        private String gender;

        @Min(0)
        private int age;

        @NotBlank
        private String seatNumber;
    }
}
