package uz.developer.zohidjon.hotel_management_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.developer.zohidjon.hotel_management_system.model.Booking;
import uz.developer.zohidjon.hotel_management_system.model.Room;
import uz.developer.zohidjon.hotel_management_system.repository.BookingRepository;
import uz.developer.zohidjon.hotel_management_system.service.impl.RoomServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {
    private final BookingRepository bookingRepository;
    private final RoomServiceImpl roomService;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void updateRoomStatusAfterCheckOut() {
        LocalDate today = LocalDate.now();
        List<Booking> expiredBookings = bookingRepository.findByCheckOutDateBefore(today);

        for (Booking booking : expiredBookings) {
            Room room = booking.getRoom();
            if (room.isBooked()) {
                room.setBooked(false);
                roomService.saveRoom(room);
                log.info("Updated room {} to available after booking expiration", room.getId());
            }
        }
    }
}