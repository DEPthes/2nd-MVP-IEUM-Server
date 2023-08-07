package depth.mvp.ieum.domain.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private static final int VERIFY_CODE_LENTH = 6;

    // 이메일로 인증 번호 보내기

    /**
     * 인증 번호 전송 메서드
     * @param email 인증 번호를 보낼 이메일
     * @return 생성된 인증 코드
     */
    public String sendVerifyCode(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        String randomeCode = generateRandomCode();

        message.setTo(email);
        message.setFrom("depth.ieum@gmail.com");
        message.setSubject("[이:음] 인증번호가 발급되었습니다.");
        message.setText(String.format("안녕하세요. 이:음입니다.\n" +
                "\n" +
                "익명으로 마음을 전하는 랜덤 익명 편지 서비스, 이:음 이용해 주셔서 감사합니다.\n" +
                "\n" +
                "아래 인증번호를 입력하여 이메일 인증을 완료해 주세요. 개인정보 보호를 위해 인증번호는 최대 3분동안 유효합니다.\n"+
                "\n" +
                "인증번호: "+ randomeCode));

        mailSender.send(message);
        return randomeCode;
    }

    // 랜덤 인증코드 생성

    /**
     * 랜덤 인증 코드 생성 메서드
     * @return 생성된 인증 코드
     */
    public static String generateRandomCode() {

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < VERIFY_CODE_LENTH; i++) {
            int digit = random.nextInt(10); // Generate a random digit (0-9)
            codeBuilder.append(digit);
        }
        log.info(codeBuilder.toString());
        return codeBuilder.toString();
    }
}
