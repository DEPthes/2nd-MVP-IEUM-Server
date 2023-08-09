package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LetterSendService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;

    @Autowired
    public LetterSendService(LetterRepository letterRepository, UserRepository userRepository) {
        this.letterRepository = letterRepository;
        this.userRepository = userRepository;
    }

    // 편지 작성
    public void writeLetter(User sender, LetterReq letterReq) {
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

    }
    // 편지 발송 시 이메일 전송

    // 편지 발송 시 수신인 랜덤 지정
    private User getRandomReceiver(User sender) {
        List<User> receivers = userRepository.findByIdNot(sender.getId());

        Random random = new Random();
        return receivers.get(random.nextInt(receivers.size()));
    }

    // 편지 발송 시 이미지 삽입 불가하게 막아두기

}
