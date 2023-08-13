package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReplyReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LetterReplyService {

    private final UserRepository userRepository;
    private final LetterRepository letterRepository;
    private final MailService mailService;

    // 편지 답장
    @Transactional
    public Letter replyLetter(UserPrincipal userPrincipal, LetterReplyReq letterReplyReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Letter originalLetter = letterRepository.findById(letterReplyReq.getOriginalLetterId())
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        // originalLetter의 receiver와 user가 동일한지 확인하는 로직
        if (!originalLetter.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");  // 적절한 문구 필요
        }

        // 편지 정보에서 발신인 id를 찾아 수신인으로 설정
        User receiver = userRepository.findById(originalLetter.getSender().getId())
                .orElseThrow(() -> new EntityNotFoundException("수신인을 찾을 수 없습니다."));

        Letter letter = Letter.builder()
                .sender(user)
                .receiver(receiver)
                .title(letterReplyReq.getTitle())
                .contents(letterReplyReq.getContents())
                .envelopType(letterReplyReq.getEnvelopType())
                .isRead(false)
                .letterType(LetterType.SENT)
                .build();

        letterRepository.save(letter);
        // 수신인에게 메일 발송
        sendEmailToReceiver(receiver.getEmail());

        return letter;

    }

    // 메일 전송 메소드
    protected void sendEmailToReceiver(String email) {
        mailService.sendEmailToReceiver(email);
    }

}
