package depth.mvp.ieum.domain.letter.domain.repository;

import depth.mvp.ieum.domain.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterRepository extends JpaRepository<Letter, Long> {
}
