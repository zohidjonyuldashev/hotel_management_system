package uz.developer.zohidjon.hotel_management_system.service;

import uz.developer.zohidjon.hotel_management_system.dto.request.BookingRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.BookingResponse;
import uz.developer.zohidjon.hotel_management_system.dto.response.PageResponse;

public interface BookingService {

    BookingResponse save(String roomId, BookingRequest request);

    BookingResponse getBookingByConfirmationCode(String confirmationCode);

    PageResponse<BookingResponse> getAllBookings(int page, int size);

    PageResponse<BookingResponse> getBookingByUserEmail(String email, int page, int size);

    void cancelBooking(String id);
}