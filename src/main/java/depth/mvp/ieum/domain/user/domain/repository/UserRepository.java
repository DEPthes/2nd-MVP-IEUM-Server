package depth.mvp.ieum.domain.user.domain.repository;

import depth.mvp.ieum.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    List<User> findByIdNot(Long id);
}
