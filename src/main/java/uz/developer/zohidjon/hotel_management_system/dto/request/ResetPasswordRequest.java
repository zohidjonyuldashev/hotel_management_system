package uz.developer.zohidjon.hotel_management_system.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.developer.zohidjon.hotel_management_system.password.StrongPassword;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    @NotEmpty(message = "Password must not be empty")
    @NotNull(message = "password must not be null")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @StrongPassword
    private String password;
    @NotEmpty(message = "Password confirmation must not be empty")
    @NotNull(message = "Password confirmation must not be null")
    private String confirmPassword;
}