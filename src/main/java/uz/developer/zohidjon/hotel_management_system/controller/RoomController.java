package uz.developer.zohidjon.hotel_management_system.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.dto.request.RoomRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.RoomResponse;
import uz.developer.zohidjon.hotel_management_system.service.RoomService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
@Tag(name = "Room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestBody RoomRequest roomRequest
    ) {
        RoomResponse response = roomService.addNewRoom(roomRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllRoomTypes() {
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType
    ) {
        List<RoomResponse> responses = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadRoomImage(
            @PathVariable String id,
            @Parameter(description = "An image is uploaded for the room.")
            @RequestPart("image") MultipartFile image) {
        roomService.uploadRoomImage(id, image);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping(value = "/{id}/get-image", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getRoomImage(@PathVariable String id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(roomService.getRoomImage(id));
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable String roomId,
            @ModelAttribute RoomRequest request) {
        return ResponseEntity.ok(roomService.update(roomId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id") String roomId) {
        RoomResponse response = roomService.getRoom(roomId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}