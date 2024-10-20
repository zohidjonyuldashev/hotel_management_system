package uz.developer.zohidjon.hotel_management_system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.developer.zohidjon.hotel_management_system.dto.request.ActivationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.AuthenticationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.RegistrationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.ResetPasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.AuthenticationResponse;
import uz.developer.zohidjon.hotel_management_system.service.AuthenticationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request) {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping(value = "/activate-account")
    public void confirm(@RequestBody @Valid ActivationRequest request) {
        service.activateAccount(request);
    }

    @PostMapping("/resend-activation-code")
    public ResponseEntity<?> resendActivationCode(@RequestParam String email) {
        service.resendActivationCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        service.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
        return ResponseEntity.ok().build();
    }
}