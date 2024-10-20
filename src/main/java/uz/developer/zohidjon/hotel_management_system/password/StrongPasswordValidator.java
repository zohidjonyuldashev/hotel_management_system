package uz.developer.zohidjon.hotel_management_system.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=`~{}\\[\\]|:\";'<>?,./]).{8,}$";
        return password.matches(passwordPattern);
    }
}
