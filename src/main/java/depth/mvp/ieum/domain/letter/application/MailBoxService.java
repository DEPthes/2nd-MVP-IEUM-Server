package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.MailBoxRes;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MailBoxService {

    private final LetterRepository letterRepository;

    // 우체통 - 받은 편지 모아보기(제목, 수신날짜, 발신자 닉네임)
    // 안 읽은 편지
    public List<MailBoxRes> getUnreadLetters(Long userId) {

        List<MailBoxRes> unreadMailBoxes = new ArrayList<>();
        List<Letter> unreadLetters = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, false, LetterType.SENT);

        for (Letter letter : unreadLetters) {
            String senderNickname = letter.getSender().getNickname();

            MailBoxRes mailBoxRes = MailBoxRes.builder()
                    .letterId(letter.getId())
                    .senderNickname(senderNickname)
                    .title(letter.getTitle())
                    .modifiedAt(letter.getModifiedAt())
                    .build();
            unreadMailBoxes.add(mailBoxRes);
        }
        return sortByLatestDate(unreadMailBoxes);
    }

    public List<MailBoxRes> getReadLetters(Long userId) {

        List<MailBoxRes> readMailBoxes = new ArrayList<>();
        List<Letter> readLetters = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, true, LetterType.SENT);

        for (Letter letter : readLetters) {
            String senderNickname = letter.getSender().getNickname();

            MailBoxRes mailBoxRes = MailBoxRes.builder()
                    .letterId(letter.getId())
                    .senderNickname(senderNickname)
                    .title(letter.getTitle())
                    .modifiedAt(letter.getModifiedAt())
                    .build();
            readMailBoxes.add(mailBoxRes);
        }
        return sortByLatestDate(readMailBoxes);
    }

    // 편지 상세 보기(읽음 여부 true 변경)
    // 기능 명세 보고 다시 수정
    // 최신 순서로 정렬
    public Letter getLetterDetails(Long userId, Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        // originalLetter의 receiver와 user가 동일한지 확인하는 로직
        if (!letter.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }

        letter.setIsRead(true);
        letterRepository.save(letter);

        return letter;
    }


    public List<MailBoxRes> sortByLatestDate(List<MailBoxRes> mailBoxes) {
        if (mailBoxes == null || mailBoxes.isEmpty()) {
            return new ArrayList<>();
        }

        Comparator<MailBoxRes> byLatestDate = Comparator.comparing(MailBoxRes::getModifiedAt).reversed();
        List<MailBoxRes> sortedMailBoxes = mailBoxes.stream()
                .sorted(byLatestDate)
                .collect(Collectors.toList());

        return sortedMailBoxes;
    }
}
