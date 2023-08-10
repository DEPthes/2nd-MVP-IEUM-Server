package depth.mvp.ieum.domain.gpt.application;

import depth.mvp.ieum.domain.gpt.dto.*;
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


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatGptService {

    private RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;

    //api key를 application.yml에 넣어두었습니다.
    @Value(value = "${api-key.chat-gpt}")
    private String apiKey;

    /**
     * Chat GPT에 닉네임 추천을 요청하는 메서드
     * @return gpt api를 호출하여 받아온 닉네임들을 배열로 반환
     */
    public RecommendRes recommendNickname() {

        List<ChatGptMessage> messages = new ArrayList<>();

        // gpt 역할 설정 (content 수정 예정)
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content("당신은 제가 요청한 대답에 미사여구 없이 답변만 해줍니다. 빠르게 답변해주세요.")
                .build());

        // 현재 DB에 있는 유저들의 닉네임들을 조회해서 String에 담는다.
        String nicknameReqest = findNicknameList();

        // 실제 요청 (content 수정 예정)
        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content("익명 서비스에서 쓰일 귀여운 닉네임을 다섯 개 추천해줘./n+" +
                        "단, " + nicknameReqest + "는 제외하고 알려줘.")
                .build());

        ChatGptRes chatGptRes = this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages,
                                ChatGptConfig.TOP_P
                        )
                )
        );

        String response = chatGptRes.getChoices().get(0).getMessage().getContent();

        // 정규식을 사용하여 띄어쓰기를 기준으로 구별하여 리스트에 추가
        List<String> listRes = new ArrayList<>();
        String[] words = response.split("\\s");
        for (String word : words) {
            if (word.matches("[가-힣]+")) {
                listRes.add(word);
            }
        }

        return RecommendRes.builder().nickname(listRes).build();
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
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60 * 1000);   //  1min = 60 sec * 1,000ms

        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<ChatGptRes> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                ChatGptRes.class);

        return responseEntity.getBody();
    }

    // 테스트 api입니다. 추후 삭제 예정입니다.
    public ChatGptRes askQuestion(QuestionReq questionRequest){

        List<ChatGptMessage> messages = new ArrayList<>();
        // gpt 역할 설정
        messages.add(ChatGptMessage.builder()
                .role("system")
                .content("당신은 감정적인 공감을 잘 하는 상담 전문가입니다. 20대 여성처럼 친근하게 답변해주세요.")
                .build());

        messages.add(ChatGptMessage.builder()
                .role(ChatGptConfig.ROLE)  // "user"
                .content(questionRequest.getQuestion())
                .build());

        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptReq(
                                ChatGptConfig.CHAT_MODEL,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.STREAM,
                                messages,
                                ChatGptConfig.TOP_P
                        )
                )
        );
    }


}