package uz.developer.zohidjon.hotel_management_system.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.dto.request.ChangePasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.UserUpdateRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.UserResponse;
import uz.developer.zohidjon.hotel_management_system.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserById(Authentication connectedUser) {
        return ResponseEntity.ok(userService.getUserById(connectedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public List<UserResponse> searchUsers(@RequestParam("searchTerm") String searchTerm) {
        return userService.searchUsers(searchTerm);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            Authentication connectedUser,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateUser(connectedUser, updateRequest));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadOrUpdateAvatar(
            Authentication connectedUser,
            @Parameter(description = "Avatar file to be uploaded")
            @RequestPart("avatar") MultipartFile file
    ) {
        userService.uploadOrUpdateAvatar(file, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "/avatar", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getAvatar(Authentication connectedUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(userService.getAvatar(connectedUser));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(Authentication connectedUser) {
        userService.deleteUser(connectedUser);
        return ResponseEntity.noContent().build();
    }
}