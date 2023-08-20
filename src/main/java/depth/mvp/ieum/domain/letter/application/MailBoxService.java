package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.MailBoxDetailsRes;
import depth.mvp.ieum.domain.letter.dto.MailBoxRes;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MailBoxService {

    private final LetterRepository letterRepository;

    // 우체통 - 받은 편지 리스트로 조회
    // 안 읽은 편지
    @Transactional
    public Page<MailBoxRes> getUnreadLetters(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Letter> unreadLettersPage = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, false, LetterType.SENT, pageable);
        List<Letter> unreadLetters = unreadLettersPage.getContent();
        List<MailBoxRes> mailBoxResList = convertLettersToMailBoxResList(unreadLetters);

        return new PageImpl<>(mailBoxResList, pageable, unreadLettersPage.getTotalElements());
    }

    @Transactional
    public Page<MailBoxRes> getReadLetters(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Letter> readLettersPage = letterRepository.findByReceiver_IdAndIsReadAndLetterType(userId, true, LetterType.SENT, pageable);
        List<Letter> readLetters = readLettersPage.getContent();
        List<MailBoxRes> mailBoxResList = convertLettersToMailBoxResList(readLetters);

        return new PageImpl<>(mailBoxResList, pageable, readLettersPage.getTotalElements());
    }

    // 편지 상세 조회(읽음 여부 true 변경)
    @Transactional
    public MailBoxDetailsRes getLetterDetails(Long userId, Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));
        if (!letter.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }
        letter.setIsRead(true);
        letterRepository.save(letter);

        return convertLetterToMailBoxDetails(letter);
    }

    private List<MailBoxRes> convertLettersToMailBoxResList(List<Letter> letters) {
        List<MailBoxRes> mailBoxResList = new ArrayList<>();

        for (Letter letter : letters) {
            String senderNickname;
            if (letter.getSender() == null) {
                senderNickname = null;
            }else {
                senderNickname = letter.getSender().getNickname();
            }

            MailBoxRes mailBoxRes = MailBoxRes.builder()
                    .letterId(letter.getId())
                    .senderNickname(senderNickname)
                    .title(letter.getTitle())
                    .envelopType(letter.getEnvelopType())
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

    private MailBoxDetailsRes convertLetterToMailBoxDetails(Letter letter) {
        String senderNickname;
        if (letter.getSender() == null) {
            senderNickname = null;
        }else {
            senderNickname = letter.getSender().getNickname();
        }
        MailBoxDetailsRes mailBoxDetailsRes = MailBoxDetailsRes.builder()
                .letterId(letter.getId())
                .senderNickname(senderNickname)
                .title(letter.getTitle())
                .contents(letter.getContents())
                .isRead(letter.isRead())
                .build();
        return mailBoxDetailsRes;
    }
}
