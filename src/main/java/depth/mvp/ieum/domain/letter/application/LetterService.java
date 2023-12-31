package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
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
    public LetterRes writeLetter(Long userId, LetterReq letterReq) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findById(userId)
                .orElseThrow();
        User receiver;
        Letter letter;
        // 임시 저장된 편지인지 확인
        if (letterReq.getLetterType() == LetterType.TEMP) {
            letter = writeTempLetter(user, letterReq);
        } else {
            receiver = getReceiver(user, letterReq.getOriginalLetterId());
            letter = Letter.builder()
                    .sender(user)
                    .receiver(receiver)
                    .title(letterReq.getTitle())
                    .contents(letterReq.getContents())
                    .envelopType(letterReq.getEnvelopType())
                    .isRead(false)
                    .isGPT(false)
                    .letterType(LetterType.SENT)
                    .build();
            letterRepository.save(letter);
            // 수신인에게 메일 발송
            sendEmailToReceiver(receiver.getEmail());
        }
        return convertLetterToLetterRes(letter);
    }

    // 메일 발송
    protected void sendEmailToReceiver(String email) throws MessagingException, UnsupportedEncodingException {
        mailService.sendEmailToReceiver(email);
    }

    // 편지 수신인 지정
    private User getReceiver(User user, Long letterId) {
        if (letterId == null) { return getRandomReceiver(user); }
        Letter originalLetter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("원본 편지를 찾을 수 없습니다."));

        validateOriginalLetterReceiver(originalLetter, user);

        // 원본 편지 정보에서 발신인 id를 찾아 수신인으로 설정
        return userRepository.findById(originalLetter.getSender().getId())
                .orElseThrow(() -> new EntityNotFoundException("수신인을 찾을 수 없습니다."));

    }

    // (편지 신규 작성) 편지 발송 시 수신인 랜덤 지정
    private User getRandomReceiver(User sender) {
        List<User> receivers = userRepository.findByIdNot(sender.getId());
        Random random = new Random();
        return receivers.get(random.nextInt(receivers.size()));
    }

    // 임시 저장된 편지 발송
    private Letter writeTempLetter(User user, LetterReq letterReq) throws MessagingException, UnsupportedEncodingException {
        Letter tempLetter = letterRepository.findById(letterReq.getLetterId())
                .orElseThrow(() -> new EntityNotFoundException("편지를 찾을 수 없습니다."));
        User receiver = tempLetter.getReceiver();

        if (receiver == null) {
            receiver = getReceiver(user, letterReq.getOriginalLetterId());
        }
        tempLetter.updateTempLetterToLetter(
                tempLetter.getId(),
                letterReq.getTitle(),
                letterReq.getContents(),
                letterReq.getEnvelopType(),
                false,
                LetterType.SENT,
                user,
                receiver
        );
        letterRepository.save(tempLetter);
        sendEmailToReceiver(receiver.getEmail());

        return tempLetter;
    }


    // (편지 답장) 원본 편지의 수신인과 현재 사용자가 동일한지 확인
    private void validateOriginalLetterReceiver(Letter originalLetter, User user) {
        if (!originalLetter.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("원본 편지의 수신자와 현재 사용자가 다릅니다.");
        }
    }

    private LetterRes convertLetterToLetterRes(Letter letter){
        LetterRes letterRes = LetterRes.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .letterType(letter.getLetterType())
                .receiverId(letter.getReceiver().getId())
                .senderId(letter.getSender().getId())
                .build();
        return letterRes;
    }
}
