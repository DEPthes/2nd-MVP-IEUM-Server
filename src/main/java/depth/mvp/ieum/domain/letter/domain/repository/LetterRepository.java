package depth.mvp.ieum.domain.letter.domain.repository;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    List<Letter> findByReceiver_IdAndIsReadAndLetterType(Long userId, boolean isRead, LetterType letterType);
    List<Letter> findBySender_IdAndLetterType(Long userId, LetterType letterType);
}
