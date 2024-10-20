package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.ActivationToken;
import uz.developer.zohidjon.hotel_management_system.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {

    @Query("FROM ActivationToken a WHERE a.user = :user AND a.invalidated = false")
    List<ActivationToken> findAllByUserAndInvalidatedFalse(User user);

    Optional<ActivationToken> findByUserAndCodeAndInvalidatedFalse(User user, String code);

    void deleteAllByUserId(String userId);
}