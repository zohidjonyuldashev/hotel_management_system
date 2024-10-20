package uz.developer.zohidjon.hotel_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.developer.zohidjon.hotel_management_system.model.Token;
import uz.developer.zohidjon.hotel_management_system.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and t.isRevoked = false \s
            """)
    List<Token> findAllValidTokenByUser(String id);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);

    void deleteByUser(User user);
}