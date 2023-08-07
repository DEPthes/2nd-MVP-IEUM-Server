package depth.mvp.ieum.domain.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import depth.mvp.ieum.domain.auth.domain.Token;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUserEmail(String userEmail);

    Optional<Token> findByRefreshToken(String refreshToken);

}