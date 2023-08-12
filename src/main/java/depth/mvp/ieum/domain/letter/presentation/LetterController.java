package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.LetterReplyService;
import depth.mvp.ieum.domain.letter.application.LetterSendService;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.dto.LetterReplyReq;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.LetterSendReq;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
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
    public ResponseEntity<?> writeLetter(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody LetterSendReq letterReq) {

        Letter letter = letterSendService.writeLetter(userPrincipal, letterReq);

        LetterRes letterRes = LetterRes.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .receiverId(letter.getReceiver().getId())
                .senderId(letter.getSender().getId())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reply")
    public ResponseEntity<?> replyLetter(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody LetterReplyReq letterReplyReq) {

        Letter letter = letterReplyService.replyLetter(userPrincipal, letterReplyReq);

        LetterRes letterSendRes = LetterRes.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .receiverId(letter.getReceiver().getId())
                .senderId(letter.getSender().getId())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterSendRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    // 이메일 전송 테스트
    // @PostMapping("/send-email")
    // public ResponseEntity<String> sendEmail(@CurrentUser UserPrincipal user) {
    //     letterSendService.sendEmailToReceiver(user.getEmail());
    //     return ResponseEntity.ok("이메일 발송에 성공했습니다.");
    // }
}
