package uz.developer.zohidjon.hotel_management_system.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.developer.zohidjon.hotel_management_system.password.StrongPassword;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    private String currentPassword;
    @StrongPassword
    private String newPassword;
    private String confirmationPassword;
}
