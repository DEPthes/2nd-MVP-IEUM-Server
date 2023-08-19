package depth.mvp.ieum.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGptConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_MODEL = "gpt-3.5-turbo";
    public static final Integer MAX_TOKEN = 300;
    public static final Integer MAX_TOKEN_LETTER = 1500;  // 편지 작성에 사용될 파라미터
    public static final Boolean STREAM = false;
    public static final String ROLE = "user";
    public static final Double TEMPERATURE = 1.0;
    public static final Double TEMPERATURE_VALID = 0.1;  // 편지 검사할 때 사용될 파라미터
//    public static final Double TOP_P = 0.9;
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    // ** 질문 리스트 **
    // 닉네임 추천 관련 세팅
    public static final String settingForNickname =
            """
                    당신은 저의 질문에 미사여구 없이 최대한 빠르게 답변만 해줍니다.
                    답변 형식 = 닉네임1,닉네임2,닉네임3,닉네임4,닉네임5,닉네임6,닉네임7,닉네임8,닉네임9,닉네임10,
                    """;
    public static final String nicknameQuestion1 =
            """
                    조건에 따라 닉네임을 10개 추천해줘. ", "로 각각의 닉네임을 구분해줘.
                    <조건>
                    1. 3~10글자의 한글로 구성되고 띄어쓰기는 없어야한다.
                    2. 형용사가 포함되어야 한다.
                    3. 동물 이름이 포함되어야 한다.
                    4. 웃기거나 귀여워야한다.
                    5. """;
    public static final String nicknameQuestion2 =
            """
                    조건에 따라 닉네임을 10개 추천해줘. ", "로 각각의 닉네임을 구분해줘.
                    <조건>
                    1. 3~10글자의 한글로 구성되고 띄어쓰기는 없어야한다.
                    2. 고유 형용사가 포함되어야 한다.
                    3. 추상 명사가 포함되어야 한다.
                    4. 웃기거나 귀여워야한다.
                    5. """;

    public static final String nicknameQuestion3 =
            """
                    조건에 따라 닉네임을 10개 추천해줘. ", "로 각각의 닉네임을 구분해줘.
                    <조건>
                    1. 3~10글자의 한글로 구성되고 띄어쓰기는 없어야한다.
                    2. 상태 형용사와 음식 이름의 조합이다.
                    3. 형용사와 음식이 어울리지 않고 웃겨야한다.
                    4. """;

    // 편지 보내기 관련 세팅
    public static final String settingForSendLetter =
            """
                    너는 편지를 답장하는 사람이야. GPT인 티를 내지말고 사람처럼 답변해줘.
                    받은 편지 내용에 공감해주고, 해결책을 제시해줘. 분량은 받은 편지의 분량만큼 작성해줘.
                    다나까를 쓰지말고 해요체를 사용해줘. 상대방을 지칭할 때는 '당신'을 사용해줘.
                    :) 같은 이모티콘도 적절하게 사용해줘.""";

    public static final String sendLetterQuestion =
            """
                    아래 편지에 받은 편지의 분량만큼 답장 편지를 써줘.
                    """;

    // 편지 검사 관련 세팅
    public static final String settingForCheckLetter =
            """
                    너는 부가적인 설명없이 답변으로 숫자 0 또는 1만 출력한다.
                    너는 감정적이고 예민한 사람이야. 너는 아래와 같은 내용의 글에 기분이 나빠지는 사람이야.
                    [기분이 나쁜 글의 내용]
                    1. 나를 모욕, 비난, 비방하는 단어 혹은 표현, 문장
                    2. 기분을 상하게 하는 단어 혹은 표현, 문장 (예를 들어 '바보' 같은 단어)
                    3. 성차별, 성희롱, 남/여 갈등을 조장하는 표현
                    4. 정치적인(정치인) 내용
                    5. 욕설
                    
                    아래 편지를 읽고 조금이라도 기분이 나쁘면 1을, 나쁘지 않으면 0을 숫자만 출력해줘.""";
}