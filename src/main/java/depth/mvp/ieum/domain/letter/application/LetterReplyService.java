package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReplyReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LetterReplyService {

    private final UserRepository userRepository;
    private final LetterRepository letterRepository;
    private final MailService mailService;

    public LetterReplyService(UserRepository userRepository, LetterRepository letterRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.letterRepository = letterRepository;
        this.mailService = mailService;
    }

    // 편지 답장
    @Transactional
    public void replyLetter(User sender, LetterReplyReq letterReplyReq) {
        Letter originalLetter = letterRepository.findById(letterReplyReq.getOriginalLetterId())
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        User receiver = userRepository.findById(originalLetter.getSender().getId())
                .orElseThrow(() -> new EntityNotFoundException("수신인을 찾을 수 없습니다."));

        Letter letter = Letter.builder()
                .sender(sender)
                .receiver(receiver)
                .title(letterReplyReq.getTitle())
                .contents(letterReplyReq.getContents())
                .envelopType(letterReplyReq.getEnvelopType())
                .isRead(false)
                .build();

        letterRepository.save(letter);
        sendEmailToReceiver(receiver.getEmail());

    }

    protected void sendEmailToReceiver(String email) {
        mailService.sendEmailToReceiver(email);
    }

}
