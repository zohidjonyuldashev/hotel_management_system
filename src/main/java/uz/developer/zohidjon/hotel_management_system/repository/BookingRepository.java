package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByConfirmationCode(String confirmationCode);

    List<Booking> findByCheckOutDateBefore(LocalDate date);
}