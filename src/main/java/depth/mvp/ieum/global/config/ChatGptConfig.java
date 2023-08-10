package depth.mvp.ieum.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGptConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_MODEL = "gpt-3.5-turbo";
    public static final Integer MAX_TOKEN = 300;
    public static final Boolean STREAM = false;
    public static final String ROLE = "user";
    public static final Double TEMPERATURE = 0.9;
    public static final Double TOP_P = 1.0;
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    // 질문 리스트
    public static final String settingForNickname = "당신은 제가 요청한 대답에 미사여구 없이 답변만 해줍니다. 빠르게 답변해주세요.";
    public static final String nicknameQuestion1 = "닉네임을 5개 추천해줘. 아래의 조건을 지켜줘./n+" +
            "1. 3~10글자의 한글로 구성되어야 한다./n+" +
            "2. 형용사가 포함되어야 한다./n+" +
            "3. 동물 이름이 포함되어야 한다./n+" +
            "4. 웃기거나 귀여워야한다./n+" +
            "5. ";

    public static final String nicknameQuestion2 = "닉네임을 5개 추천해줘. 아래의 조건을 지켜줘./n+" +
            "1. 3~10글자의 한글로 구성되어야 한다./n+" +
            "2. 고유 형용사가 포함되어야 한다./n+" +
            "3. 추상 명사가 포함되어야 한다./n+" +
            "4. 웃기거나 귀여워야한다./n+" +
            "5. ";
    public static final String nicknameQuestion3 = "닉네임을 5개 추천해줘. 아래의 조건을 지켜줘./n+" +
            "1. 3~10글자의 한글로 구성되어야 한다./n+" +
            "2. 상태 형용사와 음식 이름의 조합이다./n+" +
            "3. 형용사와 음식이 어울리지 않고 웃겨야한다./n+" +
            "4. ";

}