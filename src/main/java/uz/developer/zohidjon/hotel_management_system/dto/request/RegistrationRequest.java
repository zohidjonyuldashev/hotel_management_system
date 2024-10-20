package uz.developer.zohidjon.hotel_management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.developer.zohidjon.hotel_management_system.password.PasswordMatches;
import uz.developer.zohidjon.hotel_management_system.password.StrongPassword;

@Getter
@Setter
@Builder
@PasswordMatches
public class RegistrationRequest {

    @NotEmpty(message = "Firstname is mandatory")
    @NotNull(message = "Firstname is mandatory")
    private String firstname;
    @NotEmpty(message = "Lastname is mandatory")
    @NotNull(message = "Lastname is mandatory")
    private String lastname;
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @StrongPassword
    private String password;
    @NotEmpty(message = "Password confirmation is mandatory")
    @NotNull(message = "Password confirmation is mandatory")
    private String confirmPassword;
}