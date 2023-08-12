package depth.mvp.ieum.domain.letter.domain.repository;

import depth.mvp.ieum.domain.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByReceiver_IdAndIsRead(Long userId, boolean isRead);
}
