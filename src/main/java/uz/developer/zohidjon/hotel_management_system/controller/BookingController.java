package uz.developer.zohidjon.hotel_management_system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.developer.zohidjon.hotel_management_system.dto.request.BookingRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.BookingResponse;
import uz.developer.zohidjon.hotel_management_system.dto.response.PageResponse;
import uz.developer.zohidjon.hotel_management_system.service.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        PageResponse<BookingResponse> responses = bookingService.getAllBookings(page, size);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<BookingResponse> saveBooking(
            @PathVariable String roomId,
            @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.save(roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<BookingResponse> getBookingByConfirmationCode(
            @PathVariable String confirmationCode) {
        BookingResponse response = bookingService.getBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.ok(response);
    }
}