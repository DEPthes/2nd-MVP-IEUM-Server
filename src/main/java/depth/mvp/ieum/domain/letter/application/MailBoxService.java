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

    // 우체통 - 받은 편지 리스트로 조회
    // 안 읽은 편지
    public List<MailBoxRes> getUnreadLetters(Long userId) {
        List<Letter> unreadLetters = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, false, LetterType.SENT);
        return lettersToMailBoxResList(unreadLetters);
    }

    // 읽은 편지
    public List<MailBoxRes> getReadLetters(Long userId) {
        List<Letter> readLetters = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, true, LetterType.SENT);
        return lettersToMailBoxResList(readLetters);
    }

    // 편지 상세 조회(읽음 여부 true 변경)
    public Letter getLetterDetails(Long userId, Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        if (!letter.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }

        letter.setIsRead(true);
        letterRepository.save(letter);

        return letter;
    }

    private List<MailBoxRes> lettersToMailBoxResList(List<Letter> letters) {
        List<MailBoxRes> mailBoxResList = new ArrayList<>();

        for (Letter letter : letters) {
            String senderNickname = letter.getSender().getNickname();

            MailBoxRes mailBoxRes = MailBoxRes.builder()
                    .letterId(letter.getId())
                    .senderNickname(senderNickname)
                    .title(letter.getTitle())
                    .modifiedAt(letter.getModifiedAt())
                    .build();

            mailBoxResList.add(mailBoxRes);
        }

        return sortByLatestDate(mailBoxResList);
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
