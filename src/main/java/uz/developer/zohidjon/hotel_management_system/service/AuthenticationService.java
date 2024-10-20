package uz.developer.zohidjon.hotel_management_system.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import uz.developer.zohidjon.hotel_management_system.dto.request.ActivationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.AuthenticationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.RegistrationRequest;
import uz.developer.zohidjon.hotel_management_system.dto.request.ResetPasswordRequest;
import uz.developer.zohidjon.hotel_management_system.dto.response.AuthenticationResponse;

import java.io.IOException;

public interface AuthenticationService {
    void register(RegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void activateAccount(ActivationRequest request);

    void resendActivationCode(String email);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void forgotPassword(String email);

    void resetPassword(@Valid ResetPasswordRequest request);
}