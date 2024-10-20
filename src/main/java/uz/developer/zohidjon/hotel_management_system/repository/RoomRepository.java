package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.Room;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM Room r " +
            " WHERE r.roomType ILIKE %:roomType% " +
            " AND r.id NOT IN (" +
            "  SELECT o.room.id FROM Booking o " +
            "  WHERE ((o.checkInDate <= :checkOutDate) AND (o.checkOutDate >= :checkInDate))" +
            ") and r.isBooked = false")
    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}