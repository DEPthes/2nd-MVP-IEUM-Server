package depth.mvp.ieum.domain.verify.domain.repository;

import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.verify.domain.Verify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyRepository extends JpaRepository<Verify, Long> {

    Optional<Verify> findByCode(String code);

    Optional<Verify> findByEmail(String email);

}
