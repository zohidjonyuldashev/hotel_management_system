package uz.developer.zohidjon.hotel_management_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.developer.zohidjon.hotel_management_system.dto.request.BookingRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.BookingResponse;
import uz.developer.zohidjon.hotel_management_system.dto.response.PageResponse;
import uz.developer.zohidjon.hotel_management_system.dto.response.RoomResponse;
import uz.developer.zohidjon.hotel_management_system.exception.InvalidBookingRequestException;
import uz.developer.zohidjon.hotel_management_system.exception.ResourceNotFoundException;
import uz.developer.zohidjon.hotel_management_system.model.Booking;
import uz.developer.zohidjon.hotel_management_system.model.Room;
import uz.developer.zohidjon.hotel_management_system.repository.BookingRepository;
import uz.developer.zohidjon.hotel_management_system.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomServiceImpl roomServiceImpl;

    @Override
    public BookingResponse save(String roomId, BookingRequest request) {
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new InvalidBookingRequestException("Check-in date must be future");
        }
        Room room = roomServiceImpl.findRoomById(roomId);
        if (room.isBooked()) {
            throw new InvalidBookingRequestException("Check-in room is already booked");
        }
        room.setBooked(true);
        Room saved = roomServiceImpl.saveRoom(room);
        Booking booking = Booking.builder()
                .checkOutDate(request.getCheckOutDate())
                .checkInDate(request.getCheckInDate())
                .numOfAdults(request.getNumOfAdults())
                .numOfChildren(request.getNumOfChildren())
                .guestEmail(request.getGuestEmail())
                .guestFullName(request.getGuestFullName())
                .totalNumOfGuests(request.getNumOfAdults() + request.getNumOfChildren())
                .confirmationCode(RandomStringUtils.randomNumeric(10))
                .room(saved)
                .build();
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingByConfirmationCode(String confirmationCode) {
        Booking booking = bookingRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToBookingResponse(booking);
    }

    @Override
    public PageResponse<BookingResponse> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("checkInDate").descending());
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);
        List<BookingResponse> responses = bookingRepository.findAll()
                .stream()
                .map(this::mapToBookingResponse)
                .toList();
        return new PageResponse<>(
                responses,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isFirst(),
                bookingPage.isLast()
        );
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        RoomResponse roomResponse = roomServiceImpl.mapToRoomResponse(booking.getRoom());
        return BookingResponse.builder()
                .id(booking.getId())
                .checkOutDate(booking.getCheckOutDate())
                .checkInDate(booking.getCheckInDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .guestEmail(booking.getGuestEmail())
                .guestFullName(booking.getGuestFullName())
                .totalNumOfGuests(booking.getTotalNumOfGuests())
                .confirmationCode(booking.getConfirmationCode())
                .roomResponse(roomResponse)
                .build();
    }
}
