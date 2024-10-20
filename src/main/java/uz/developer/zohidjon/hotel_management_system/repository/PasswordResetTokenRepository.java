package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.PasswordResetToken;
import uz.developer.zohidjon.hotel_management_system.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenAndInvalidatedFalse(String token);

    List<PasswordResetToken> findByUserAndInvalidatedFalse(User user);
}