package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    // 편지 작성
    @Transactional
    public Letter writeLetter(UserPrincipal userPrincipal, LetterReq letterReq) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow();

        Letter letter;
        User receiver;

        if (letterReq.getOriginalLetterId() == null) {
            // 편지 신규 작성
            receiver = getRandomReceiver(user);
        } else {
            // 편지 답장
            Letter originalLetter = getOriginalLetterAndCheckReceiver(letterReq.getOriginalLetterId(), user);

            // 원본 편지 정보에서 발신인 id를 찾아 수신인으로 설정
            receiver = userRepository.findById(originalLetter.getSender().getId())
                    .orElseThrow(() -> new EntityNotFoundException("수신인을 찾을 수 없습니다."));
        }

        letter = Letter.builder()
                .sender(user)
                .receiver(receiver)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .isRead(false)
                .letterType(LetterType.SENT)
                .build();

        letterRepository.save(letter);

        // 수신인에게 메일 발송
        sendEmailToReceiver(receiver.getEmail());
        return letter;
    }

    // 메일 발송
    protected void sendEmailToReceiver(String email) {
        mailService.sendEmailToReceiver(email);
    }

    // (퍈지 신규 작성) 편지 발송 시 수신인 랜덤 지정
    private User getRandomReceiver(User sender) {
        List<User> receivers = userRepository.findByIdNot(sender.getId());

        Random random = new Random();
        return receivers.get(random.nextInt(receivers.size()));
    }

    // (편지 답장) 원본 편지의 수신인과 현재 사용자가 동일한지 확인
    private Letter getOriginalLetterAndCheckReceiver(Long originalLetterId, User user) {
        Letter originalLetter = letterRepository.findById(originalLetterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        if (!originalLetter.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }

        return originalLetter;
    }


}
