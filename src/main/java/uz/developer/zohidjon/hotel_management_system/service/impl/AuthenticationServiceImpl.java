package uz.developer.zohidjon.hotel_management_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.developer.zohidjon.hotel_management_system.dto.request.ActivationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.AuthenticationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.RegistrationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.ResetPasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.AuthenticationResponse;
import uz.developer.zohidjon.hotel_management_system.exception.ActivationTokenException;
import uz.developer.zohidjon.hotel_management_system.model.*;
import uz.developer.zohidjon.hotel_management_system.repository.ActivationTokenRepository;
import uz.developer.zohidjon.hotel_management_system.repository.PasswordResetTokenRepository;
import uz.developer.zohidjon.hotel_management_system.repository.TokenRepository;
import uz.developer.zohidjon.hotel_management_system.repository.UserRepository;
import uz.developer.zohidjon.hotel_management_system.service.AuthenticationService;
import uz.developer.zohidjon.hotel_management_system.service.EmailService;
import uz.developer.zohidjon.hotel_management_system.service.JwtService;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void register(RegistrationRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new IllegalArgumentException("Email is already in use");
        });
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .accountLocked(false)
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var accessToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(accessToken, refreshToken, user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public void activateAccount(ActivationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
        ActivationToken savedActivationToken = activationTokenRepository
                .findByUserAndCodeAndInvalidatedFalse(user, request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));
        if (LocalDateTime.now().isAfter(savedActivationToken.getExpiresAt())) {
            throw new ActivationTokenException("Activation token has expired.");
        }

        user.setEnabled(true);
        userRepository.save(user);

        savedActivationToken.setValidatedAt(LocalDateTime.now());
        savedActivationToken.setInvalidated(true); // Invalidate the token
        activationTokenRepository.save(savedActivationToken);
    }

    @Override
    public void resendActivationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        invalidatePreviousTokens(user);
        sendValidationEmail(user);
    }

    @Override
    public void forgotPassword(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        invalidatePreviousPasswordTokens(user);
        var resetToken = generatePasswordResetToken(user);
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetToken);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        var resetToken = passwordResetTokenRepository.findByTokenAndInvalidatedFalse(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new RuntimeException("Reset token has expired.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalStateException("Password and confirmation password are not the same");
        }

        var user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        resetToken.setInvalidated(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Refresh token is missing or invalid");
            return;
        }
        final String refreshToken = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid refresh token");
            return;
        }

        User userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean validRefreshToken = tokenRepository.findByRefreshToken(refreshToken)
                .map(t -> !t.isRevoked())
                .orElse(false);
        if (jwtService.isTokenValid(refreshToken, userDetails) && validRefreshToken) {
            String accessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);
            revokeAllUserTokens(userDetails);
            saveUserToken(accessToken, newRefreshToken, userDetails);
            AuthenticationResponse authResponse = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .build();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("Refresh token is invalid");
        }

    }

    private String generatePasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        var resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .createdDate(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .invalidated(false)
                .build();
        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    private void invalidatePreviousPasswordTokens(User user) {
        var tokens = passwordResetTokenRepository.findByUserAndInvalidatedFalse(user);
        tokens.forEach(token -> {
            token.setInvalidated(true);
            passwordResetTokenRepository.save(token);
        });
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var activationCode = ActivationToken.builder()
                .code(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .invalidated(false)
                .build();
        activationTokenRepository.save(activationCode);
        return generatedToken;
    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.getFullName(),
                    newToken,
                    "Account activation"
            );
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    private void invalidatePreviousTokens(User user) {
        List<ActivationToken> tokens = activationTokenRepository.findAllByUserAndInvalidatedFalse(user);
        for (ActivationToken token : tokens) {
            token.setInvalidated(true);
            activationTokenRepository.save(token);
        }
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setRevoked(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(validUserTokens);
    }
}
