package uz.developer.zohidjon.hotel_management_system.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.dto.request.ChangePasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.UserUpdateRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.UserResponse;

import java.security.Principal;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Authentication connectedUser);

    void uploadOrUpdateAvatar(MultipartFile file, Authentication connectedUser);

    UserResponse updateUser(Authentication connectedUser, UserUpdateRequest updateRequest);

    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    byte[] getAvatar(Authentication connectedUser);

    void deleteUser(Authentication connectedUser);

    List<UserResponse> searchUsers(String searchTerm);
}