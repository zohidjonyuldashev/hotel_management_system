package uz.developer.zohidjon.hotel_management_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.config.FileUtils;
import uz.developer.zohidjon.hotel_management_system.dto.request.RoomRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.RoomResponse;
import uz.developer.zohidjon.hotel_management_system.exception.ResourceNotFoundException;
import uz.developer.zohidjon.hotel_management_system.model.Room;
import uz.developer.zohidjon.hotel_management_system.repository.RoomRepository;
import uz.developer.zohidjon.hotel_management_system.service.FileStorageService;
import uz.developer.zohidjon.hotel_management_system.service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;

    @Override
    public RoomResponse addNewRoom(RoomRequest request) {
        Room room = Room.builder()
                .roomType(request.getRoomType())
                .roomPrice(request.getRoomPrice())
                .isBooked(false)
                .build();

        Room saved = saveRoom(room);
        return mapToRoomResponse(saved);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoom(String roomId) {
        Room room = findRoomById(roomId);
        roomRepository.delete(room);
    }

    @Override
    public RoomResponse update(String roomId, RoomRequest request) {
        Room room = findRoomById(roomId);
        room.setRoomType(request.getRoomType());
        room.setRoomPrice(request.getRoomPrice());
        Room saved = saveRoom(room);
        return mapToRoomResponse(saved);
    }

    @Override
    public RoomResponse getRoom(String roomId) {
        Room room = findRoomById(roomId);
        return mapToRoomResponse(room);
    }

    @Override
    public void uploadRoomImage(String id, MultipartFile image) {
        Room room = findRoomById(id);
        String imageUrl = fileStorageService.saveRoomImage(image, room);
        room.setPhotoUrl(imageUrl);
        saveRoom(room);
    }

    @Override
    public byte[] getRoomImage(String id) {
        Room room = findRoomById(id);
        return FileUtils.readFileFromLocation(room.getPhotoUrl());
    }

    @Override
    public List<RoomResponse> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
        return availableRooms.stream().map(this::mapToRoomResponse).collect(Collectors.toList());
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room findRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    public RoomResponse mapToRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .roomPrice(room.getRoomPrice())
                .isBooked(room.isBooked())
                .photo_url(room.getPhotoUrl())
                .build();
    }
}