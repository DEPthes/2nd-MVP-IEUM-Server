package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterSendReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class LetterSendService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    // 편지 작성
    @Transactional
    public Letter writeLetter(UserPrincipal userPrincipal, LetterSendReq letterReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow();
        User receiver = getRandomReceiver(user);

        Letter letter = Letter.builder()
                .sender(user)
                .receiver(receiver)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .isRead(false)
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

    // 편지 발송 시 수신인 랜덤 지정
    private User getRandomReceiver(User sender) {
        List<User> receivers = userRepository.findByIdNot(sender.getId());

        Random random = new Random();
        return receivers.get(random.nextInt(receivers.size()));
    }


}
