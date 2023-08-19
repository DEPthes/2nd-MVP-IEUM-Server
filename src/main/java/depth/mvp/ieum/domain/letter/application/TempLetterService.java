package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
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
@Transactional(readOnly = true)
public class TempLetterService {

    private final UserRepository userRepository;
    private final LetterRepository letterRepository;


    @Transactional
    public LetterRes writeTempLetter(Long userId, LetterReq letterReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Letter originalLetter = letterRepository.findById(letterReq.getOriginalLetterId())
                .orElseThrow();
        User receiver = getReceiver(user, letterReq.getOriginalLetterId());

        Letter letter;
        if (originalLetter.isGPT()) {
            letter = Letter.builder()
                    .sender(user)
                    .receiver(receiver)
                    .title(letterReq.getTitle())
                    .contents(letterReq.getContents())
                    .envelopType(letterReq.getEnvelopType())
                    .isRead(false)
                    .isGPT(true)
                    .letterType(LetterType.TEMP)
                    .build();
        } else {
            letter = Letter.builder()
                    .sender(user)
                    .receiver(receiver)
                    .title(letterReq.getTitle())
                    .contents(letterReq.getContents())
                    .envelopType(letterReq.getEnvelopType())
                    .isRead(false)
                    .isGPT(false)
                    .letterType(LetterType.TEMP)
                    .build();
        }
        letterRepository.save(letter);
        return convertLetterToLetterRes(letter);
    }

    // 임시저장 목록 조회(답장/신규 구분)
    // 신규 작성
    public List<TempLetterRes> getNewTempLetters(Long userId) {
        List<TempLetterRes> newTempLetters = new ArrayList<>();
        List<Letter> letters = letterRepository.findBySender_IdAndIsGPTAndLetterType(userId, false, LetterType.TEMP);

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
    public List<TempLetterRes> getReplyTempLetters(Long userId, Long originalLetterId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Letter originalLetter = letterRepository.findById(originalLetterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));
        validateOriginalLetterReceiver(originalLetter, user);

        List<TempLetterRes> replyTempLetters = new ArrayList<>();

        if (originalLetter.isGPT()) {   // chatgpt 답장
            List<Letter> letters = letterRepository.findBySender_IdAndIsGPTAndLetterType(userId, true, LetterType.TEMP);
            for (Letter letter : letters) {
                TempLetterRes tempLetterRes = createTempLetterRes(letter);
                replyTempLetters.add(tempLetterRes);
            }
        }
        else {    // 일반 답장
            Long receiverId = originalLetter.getSender().getId();
            List<Letter> letters = letterRepository.findBySender_IdAndReceiver_IdAndLetterType(userId, receiverId, LetterType.TEMP);

            for (Letter letter : letters) {
                TempLetterRes tempLetterRes = createTempLetterRes(letter);
                replyTempLetters.add(tempLetterRes);
            }
        }
        return sortByLatestDate(replyTempLetters);
    }

    // 임시 저장된 편지 불러오기
    public LetterRes getTempLetter(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다."));
        return convertLetterToLetterRes(letter);
    }

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
        // 챗지피티의 경우
        if (originalLetter.getSender() == null) {
            return null;
        }
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
