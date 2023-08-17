package depth.mvp.ieum.domain.letter.application;

import depth.mvp.ieum.domain.gpt.application.ChatGptService;
import depth.mvp.ieum.domain.gpt.dto.ChatGptMessage;
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
import depth.mvp.ieum.global.config.ChatGptConfig;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class LetterGptService {

    private final ChatGptService chatGptService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final LetterRepository letterRepository;

    // 신규 편지 작성 - gpt에게
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

    // gpt에게 편지를 보내고 답장을 받는 메서드 (신규 작성, 비동기 처리)
    @Transactional
    public void asyncSendLetterToGpt(Letter letterFromUser, User user) {

        // GPT API 비동기 호출
        CompletableFuture.runAsync(() -> {
            LetterRes letterRes = chatGptService.sendLetter(letterFromUser);
            mailService.sendEmailToReceiver(user.getEmail());
            Letter letterFromGpt = Letter.builder()
                    .receiver(user)
                    .title(letterRes.getData().substring(0,8))
                    .contents(letterRes.getData())
                    .envelopType(letterFromUser.getEnvelopType())
                    .letterType(LetterType.SENT)   // LetterType.SENT 지정
                    .isRead(false)
                    .build();
            letterRepository.save(letterFromGpt);
        });
    }

    // 답장 편지 작성 - gpt
    @Transactional
    public void replyLetterForGpt(UserPrincipal userPrincipal, LetterReq letterReq) {

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        Letter letter = Letter.builder()
                .sender(findUser)
                .title(letterReq.getTitle())
                .contents(letterReq.getContents())
                .envelopType(letterReq.getEnvelopType())
                .letterType(LetterType.SENT)
                .isRead(true)
                .build();

        letterRepository.save(letter);
        asyncReplyLetterToGpt(letter, findUser);
    }

    // gpt에게 편지를 보내고 답장을 받는 메서드 (답장, 비동기 처리)
    @Transactional
    public void asyncReplyLetterToGpt(Letter letterFromUser, User user) {
        List<Letter> messageList = letterRepository.findBySender_IdOrReceiver_Id(user.getId(), user.getId());
        List<ChatGptMessage> gptMessageList = createMessageList(messageList);
        // GPT API 비동기 호출
        CompletableFuture.runAsync(() -> {
            LetterRes letterRes = chatGptService.replyLetter(letterFromUser, gptMessageList);
            mailService.sendEmailToReceiver(user.getEmail());
            Letter letterFromGpt = Letter.builder()
                    .receiver(user)
                    .title(letterRes.getData().substring(0,8) + "..")
                    .contents(letterRes.getData())
                    .envelopType(letterFromUser.getEnvelopType())
                    .letterType(LetterType.SENT)   // LetterType.SENT 지정
                    .isRead(false)
                    .build();
            letterRepository.save(letterFromGpt);
        });
    }

    // gpt를 통한 편지 검사
    public String checkLetter(UserPrincipal userPrincipal, LetterCheckReq letterCheckReq) {

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");

        return chatGptService.checkLetter(letterCheckReq);
    }

    public static List<ChatGptMessage> createMessageList(List<Letter> letterList) {
        List<ChatGptMessage> messages = new ArrayList<>();
        for(Letter letter : letterList){
            // 유저 -> GPT의 편지
            if (letter.getReceiver() == null) {
                messages.add(ChatGptMessage.builder()
                        .role("user")
                        .content(letter.getTitle() + "\n" +letter.getContents())
                        .build());
            }
            // GPT -> 유저의 편지
            else {
                messages.add(ChatGptMessage.builder()
                        .role("assistant")
                        .content(letter.getTitle() + "\n" +letter.getContents())
                        .build());
            }
        }
        return messages;
    }
}
