package depth.mvp.ieum.domain.gpt.application;

import depth.mvp.ieum.domain.gpt.dto.*;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.dto.LetterCheckReq;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.config.ChatGptConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatGptService {

    private RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;

    @Value(value = "${api-key.chat-gpt}")
    private String apiKey;

    /**
     * Chat GPT에 닉네임 추천을 요청하는 메서드
     * @return gpt api를 호출하여 받아온 닉네임들을 배열로 반환
     */
    public RecommendRes recommendNickname() {

        List<ChatGptMessage> messages = new ArrayList<>();

        // gpt 역할 설정
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content(ChatGptConfig.settingForNickname)
                .build());

        // 실제 요청
        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content(createQuestionForNicknmae())
                .build());

        log.info(messages.toString());
        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        );

        String response = chatGptRes.getChoices().get(0).getMessage().getContent();

        return RecommendRes.builder().nickname(createPrettyResponseForNickname(response)).build();
    }

    /**
     * GPT에게 편지 전송
     * @param letter 전송할 편지 객체
     */
    public LetterRes sendLetter(Letter letter) {

        List<ChatGptMessage> messages = new ArrayList<>();

        // gpt 역할 설정
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content(ChatGptConfig.settingForSendLetter)
                .build());

        // 실제 요청 (content 수정 예정)
        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content(ChatGptConfig.sendLetterQuestion + "\n" + letter.getTitle() + "\n" +letter.getContents())
                .build());

        log.info(messages.toString());
        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN_LETTER,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        );

        String response = chatGptRes.getChoices().get(0).getMessage().getContent();

        return LetterRes.builder().data(response).build();
    }

    public LetterRes replyLetter(Letter letter, List<ChatGptMessage> messageList) {
        List<ChatGptMessage> messages = new ArrayList<>();

        // gpt 역할 설정
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content(ChatGptConfig.settingForSendLetter)
                .build());
        messages.addAll(messageList);
        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content(ChatGptConfig.sendLetterQuestion + "\n" + letter.getTitle() + "\n" +letter.getContents())
                .build());
        log.info(messages.toString());
        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN_LETTER,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        );

        String response = chatGptRes.getChoices().get(0).getMessage().getContent();

        return LetterRes.builder().data(response).build();
    }


    // GPT를 통한 편지 검사
    public String checkLetter(LetterCheckReq letterCheckReq) {

        List<ChatGptMessage> messages = new ArrayList<>();

        // gpt 역할 설정
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content(ChatGptConfig.settingForCheckLetter)
                .build());

        // 실제 요청 (content 수정 예정)
        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content(letterCheckReq.getTitle() + letterCheckReq.getContents())
                .build());

        log.info(messages.toString());
        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE_VALID,
                                ChatGptConfig.STREAM,
                                messages
                        )
                )
        );

        return chatGptRes.getChoices().get(0).getMessage().getContent();
    }

    /**
     * DB에서 유저들의 닉네임을 조회하는 메서드
     * @return 닉네임 사이에 ','를 추가해서 문자열로 만들어 반환
     */
    public String findNicknameList() {
        List<User> userList = userRepository.findAll();
        List<String> nicknameList = new ArrayList<>();
        for (User user : userList) {
            nicknameList.add(user.getNickname());
        }
        return String.join(", ", nicknameList);
    }

    // 닉네임 추천 질문 만들기 메서드
    public String createQuestionForNicknmae() {
        // 현재 DB에 있는 유저들의 닉네임들을 조회해서 String에 담는다.
        String nicknameList = findNicknameList();
        String nicknameReqest = nicknameList.length() != 0 ? nicknameList+"은 제외하고 추천해줘." : "";
        // 질문 만들기
        String question = getRandomQuestion(ChatGptConfig.nicknameQuestion1, ChatGptConfig.nicknameQuestion2, ChatGptConfig.nicknameQuestion3);
        question += nicknameReqest;
        return question;
    }

    // 랜덤 질문 생성 메서드
    public static String getRandomQuestion(String... questions) {
        Random random = new Random();
        int randomIndex = random.nextInt(questions.length);
        return questions[randomIndex];
    }

    // 프론트로 응답 주기 전 데이터를 정제하기 위한 메서드
    public List<String> createPrettyResponseForNickname(String response) {
        // 정규식을 사용하여 띄어쓰기를 기준으로 구별하여 리스트에 추가
        List<String> listRes = new ArrayList<>();
        String[] words = response.split("\\s");
        for (String word : words) {
            if (word.matches("[가-힣]+")) {
                listRes.add(word);
            }
        }
        return listRes;
    }

    // api 호출에 필요한 Http Header를 만드는 메서드
    public HttpEntity<ChatGptReq> buildHttpEntity(ChatGptReq chatGptRequest){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        httpHeaders.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + apiKey);
        return new HttpEntity<>(chatGptRequest, httpHeaders);
    }

    // 실제 gpt api 요청후 response body를 받아오는 메서드
    public ChatGptRes getResponse(HttpEntity<ChatGptReq> chatGptRequestHttpEntity){

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 답변이 길어질 경우 TimeOut Error 발생하므로 time 설정
        requestFactory.setConnectTimeout(180000);
        requestFactory.setReadTimeout(180000);   //  3min

        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<ChatGptRes> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                ChatGptRes.class);

        return responseEntity.getBody();
    }
}