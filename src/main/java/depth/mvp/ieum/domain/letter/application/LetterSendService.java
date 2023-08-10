package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterSendReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class LetterSendService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public LetterSendService(LetterRepository letterRepository, UserRepository userRepository, MailService mailService) {
        this.letterRepository = letterRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    // 편지 작성 및 수신자에게 이메일 발송
    @Transactional
    public void writeLetter(User sender, LetterSendReq letterReq) {
        User receiver = getRandomReceiver(sender);

        Letter letter = Letter.builder()
                .sender(sender)
                .receiver(receiver)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .isRead(false)
                .build();

        letterRepository.save(letter);
        sendEmailToReceiver(receiver.getEmail());

    }

    // 이메일 전송
    protected void sendEmailToReceiver(String email) {
        mailService.sendEmailToReceiver(email);
    }

    // 편지 발송 시 수신인 랜덤 지정
    private User getRandomReceiver(User sender) {
        List<User> receivers = userRepository.findByIdNot(sender.getId());

        Random random = new Random();
        return receivers.get(random.nextInt(receivers.size()));
    }

    // 편지 발송 시 이미지 삽입 불가하게 막아두기

}
