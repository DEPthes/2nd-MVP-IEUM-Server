package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.gpt.application.ChatGptService;
import depth.mvp.ieum.domain.gpt.dto.LetterRes;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.domain.LetterType;
import depth.mvp.ieum.domain.letter.domain.repository.LetterRepository;
import depth.mvp.ieum.domain.letter.dto.LetterCheckReq;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.mail.MailService;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.DefaultAssert;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class LetterGptService {

    private final ChatGptService chatGptService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final LetterRepository letterRepository;

    // 편지 작성 - gpt에게
    @Transactional
    public void writeLetterForGpt(UserPrincipal userPrincipal, LetterReq letterReq) {

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        // 수신인은 gpt이므로 receiver는 null, isRead는 true로 생성
        // 추가) 발송되는 편지이므로 LetterType.SENT 지정
        Letter letter = Letter.builder()
                .sender(findUser)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .letterType(LetterType.SENT)
                .isRead(true)
                .build();

        letterRepository.save(letter);
        asyncSendLetterToGpt(letter, findUser);
    }

    // gpt에게 편지를 보내고 답장을 받는 메서드 (비동기 처리)
    @Transactional
    public void asyncSendLetterToGpt(Letter letterFromUser, User user) {

        // GPT API 비동기 호출
        CompletableFuture.runAsync(() -> {
            LetterRes letterRes = chatGptService.sendLetter(letterFromUser);
            log.info(letterRes.toString());
            mailService.sendEmailToReceiver(user.getEmail());
            Letter letterFromGpt = Letter.builder()
                    .receiver(user)
                    .title(extractTitle(letterRes.getData()))
                    .contents(extractContent(letterRes.getData()))
                    .envelopType(letterFromUser.getEnvelopType())
                    .letterType(LetterType.SENT)   // LetterType.SENT 지정
                    .isRead(false)
                    .build();
            letterRepository.save(letterFromGpt);
        });
    }

    // gpt의 편지 답장에서 제목을 추출하는 메서드
    public static String extractTitle(String letter) {
        Pattern pattern = Pattern.compile("\\[제목\\] (.*?)\\n");
        Matcher matcher = pattern.matcher(letter);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    // gpt의 편지 답장에서 내용을 추출하는 메서드
    public static String extractContent(String letter) {
        Pattern pattern =Pattern.compile("\\[내용\\] (.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(letter);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    // gpt를 통한 편지 검사
    public String checkLetter(UserPrincipal userPrincipal, LetterCheckReq letterCheckReq) {

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");

        return chatGptService.checkLetter(letterCheckReq);
    }
}
