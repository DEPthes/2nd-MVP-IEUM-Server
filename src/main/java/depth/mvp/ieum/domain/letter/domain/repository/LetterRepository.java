package depth.mvp.ieum.domain.letter.domain.repository;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    Page<Letter> findByReceiver_IdAndIsReadAndLetterType(Long userId, boolean isRead, LetterType letterType, Pageable pageable);
    List<Letter> findBySender_IdAndLetterType(Long userId, LetterType letterType);

    List<Letter> findBySender_IdOrReceiver_Id(Long sender_id, Long receiver_id);

    List<Letter> findBySender_IdAndIsGPTAndLetterType(Long userId, boolean b, LetterType temp);

    List<Letter> findBySender_IdAndReceiver_IdAndLetterType(Long senderId, Long receiverId, LetterType temp);
}
