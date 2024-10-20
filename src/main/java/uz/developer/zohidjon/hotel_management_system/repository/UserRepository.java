package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query("FROM User u WHERE " +
            "u.email ilike %:searchTerm% or u.id ilike %:searchTerm%")
    List<User> findByEmailOrId(@Param("searchTerm") String searchTerm);
}