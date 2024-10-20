package uz.developer.zohidjon.hotel_management_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.developer.zohidjon.hotel_management_system.config.FileUtils;
import uz.developer.zohidjon.hotel_management_system.dto.request.ChangePasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.UserUpdateRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.UserResponse;
import uz.developer.zohidjon.hotel_management_system.model.User;
import uz.developer.zohidjon.hotel_management_system.repository.ActivationTokenRepository;
import uz.developer.zohidjon.hotel_management_system.repository.TokenRepository;
import uz.developer.zohidjon.hotel_management_system.repository.UserRepository;
import uz.developer.zohidjon.hotel_management_system.service.FileStorageService;
import uz.developer.zohidjon.hotel_management_system.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final ActivationTokenRepository activationTokenRepository;
    private final TokenRepository tokenRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return mapToUserResponse(user);
    }

    @Override
    public void uploadOrUpdateAvatar(MultipartFile file, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            String profileAvatar = fileStorageService.saveFile(file, user.getId(), "user");
            user.setAvatarUrl(profileAvatar);
        } else {
            String updatedAvatarUrl = fileStorageService.updateAvatar(file, user);
            user.setAvatarUrl(updatedAvatarUrl);
        }
        userRepository.save(user);
    }

    @Override
    public UserResponse updateUser(Authentication connectedUser, UserUpdateRequest updateRequest) {
        User user = (User) connectedUser.getPrincipal();

        user.setFirstname(updateRequest.getFirstname());
        user.setLastname(updateRequest.getLastname());

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password and confirmation password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public byte[] getAvatar(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return FileUtils.readFileFromLocation(user.getAvatarUrl());
    }

    @Override
    public List<UserResponse> searchUsers(String searchTerm) {
        List<User> userList = userRepository.findByEmailOrId(searchTerm);
        return userList.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    @Transactional
    public void deleteUser(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        activationTokenRepository.deleteAllByUserId(user.getId());
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }
}


