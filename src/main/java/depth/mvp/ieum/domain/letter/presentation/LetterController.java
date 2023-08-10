package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.LetterReplyService;
import depth.mvp.ieum.domain.letter.application.LetterSendService;
import depth.mvp.ieum.domain.letter.dto.LetterReplyReq;
import depth.mvp.ieum.domain.letter.dto.LetterSendReq;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/letter")
public class LetterController {

    private final LetterSendService letterSendService;
    private final LetterReplyService letterReplyService;

    @PostMapping("/send")
    public ResponseEntity<String> writeLetter(@CurrentUser UserPrincipal user, @Valid @RequestBody LetterSendReq letterReq) {
        letterSendService.writeLetter(user.getUser(), letterReq);
        return ResponseEntity.ok("편지 발송에 성공했습니다.");
    }

    @PostMapping("/reply")
    public ResponseEntity<String> replyLetter(@CurrentUser UserPrincipal user, @Valid @RequestBody LetterReplyReq letterReplyReq) {
        letterReplyService.replyLetter(user.getUser(), letterReplyReq);
        return ResponseEntity.ok("편지 발송에 성공했습니다.");
    }


    // 이메일 전송 테스트
    // @PostMapping("/send-email")
    // public ResponseEntity<String> sendEmail(@CurrentUser UserPrincipal user) {
    //     letterSendService.sendEmailToReceiver(user.getEmail());
    //     return ResponseEntity.ok("이메일 발송에 성공했습니다.");
    // }
}
