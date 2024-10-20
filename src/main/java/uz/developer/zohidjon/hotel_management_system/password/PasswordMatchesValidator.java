package uz.developer.zohidjon.hotel_management_system.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uz.developer.zohidjon.hotel_management_system.dto.request.RegistrationRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequest> {
    @Override
    public boolean isValid(RegistrationRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            return false;
        }
        return request.getPassword().equals(request.getConfirmPassword());
    }
}