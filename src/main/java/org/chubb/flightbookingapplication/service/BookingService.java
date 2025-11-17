package org.chubb.flightbookingapplication.service;



import org.chubb.flightbookingapplication.dto.BookingRequest;
import org.chubb.flightbookingapplication.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse bookTicket(BookingRequest request);

    BookingResponse getTicketByPnr(String pnr);

    List<BookingResponse> getBookingHistory(String email);

    void cancelBooking(String pnr);
}
