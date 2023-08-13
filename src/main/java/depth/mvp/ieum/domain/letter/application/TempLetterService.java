package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.MailBoxRes;
import depth.mvp.ieum.domain.letter.dto.TempLetterRes;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TempLetterService {

    private final UserRepository userRepository;
    private final LetterRepository letterRepository;

    // 서비스만 나누고 컨트롤러는 letterController에

    @Transactional
    public LetterRes writeTempLetter(Long userId, LetterReq letterReq) {

        User user = userRepository.findById(userId)
                .orElseThrow();
        User receiver = getReceiver(user, letterReq.getOriginalLetterId());

        Letter letter = Letter.builder()
                .sender(user)
                .receiver(receiver)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .isRead(false)
                .letterType(LetterType.TEMP)
                .build();

        letterRepository.save(letter);
        return convertLetterToLetterRes(letter);
    }

    // 임시저장 조회(답장/신규 구분)
    // 신규 작성
    public List<TempLetterRes> getNewTempLetters(Long userId) {
        List<TempLetterRes> newTempLetters = new ArrayList<>();
        List<Letter> letters = letterRepository.findBySender_IdAndLetterType(userId, LetterType.TEMP);

        for (Letter letter : letters) {
            boolean isReceiverNull = (letter.getReceiver() == null);
            if (isReceiverNull) {
                TempLetterRes tempLetterRes = createTempLetterRes(letter);
                newTempLetters.add(tempLetterRes);
            }
        }
        return sortByLatestDate(newTempLetters);

    }

    // 답장
    public List<TempLetterRes> getReplyTempLetters(Long userId) {
        List<TempLetterRes> replyTempLetters = new ArrayList<>();
        List<Letter> letters = letterRepository.findBySender_IdAndLetterType(userId, LetterType.TEMP);

        for (Letter letter : letters) {
            boolean isReceiverNull = (letter.getReceiver() == null);
            if (!isReceiverNull) {
                TempLetterRes tempLetterRes = createTempLetterRes(letter);
                replyTempLetters.add(tempLetterRes);
            }
        }
        return sortByLatestDate(replyTempLetters);
    }

    // 임시저장 불러오기

    private TempLetterRes createTempLetterRes(Letter letter) {
        return TempLetterRes.builder()
                .letterId(letter.getId())
                .title(letter.getTitle())
                .modifiedAt(letter.getModifiedAt())
                .build();
    }

    private User getReceiver(User user, Long letterId) {
        // (임시저장) 편지 신규 작성
        if (letterId == null) { return null; }
        // (임시저장) 편지 답장
        Letter originalLetter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        validateOriginalLetterReceiver(originalLetter, user);

        // 원본 편지 정보에서 발신인 id를 찾아 수신인으로 설정
        return userRepository.findById(originalLetter.getSender().getId())
                .orElseThrow(() -> new EntityNotFoundException("수신인을 찾을 수 없습니다."));

    }

    private void validateOriginalLetterReceiver(Letter originalLetter, User user) {
        if (!originalLetter.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }
    }

    public List<TempLetterRes> sortByLatestDate(List<TempLetterRes> tempLetterRes) {
        if (tempLetterRes == null || tempLetterRes.isEmpty()) {
            return new ArrayList<>();
        }
        Comparator<TempLetterRes> byLatestDate = Comparator.comparing(TempLetterRes::getModifiedAt).reversed();
        List<TempLetterRes> sortedTempLetterRes = tempLetterRes.stream()
                .sorted(byLatestDate)
                .collect(Collectors.toList());

        return sortedTempLetterRes;
    }

    private LetterRes convertLetterToLetterRes(Letter letter){
        Long receiverId = null;
        if (letter.getReceiver() != null) {
            receiverId = letter.getReceiver().getId();
        }
        LetterRes letterRes = LetterRes.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .letterType(letter.getLetterType())
                .receiverId(receiverId)
                .senderId(letter.getSender().getId())
                .build();
        return letterRes;
    }

}
