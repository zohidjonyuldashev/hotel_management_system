package uz.developer.zohidjon.hotel_management_system.service;

import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.dto.request.RoomRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.RoomResponse;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    RoomResponse addNewRoom(RoomRequest roomRequest);

    List<String> getAllRoomTypes();

    List<RoomResponse> getAllRooms();

    void deleteRoom(String roomId);

    RoomResponse update(String roomId, RoomRequest request);

    RoomResponse getRoom(String roomId);

    void uploadRoomImage(String id, MultipartFile image);

    byte[] getRoomImage(String id);

    List<RoomResponse> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
