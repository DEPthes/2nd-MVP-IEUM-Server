package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.MailBoxService;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.MailBoxDetailsRes;
import depth.mvp.ieum.domain.letter.dto.MailBoxRes;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mailbox")
public class MailBoxController {

    private final MailBoxService mailBoxService;

    @GetMapping
    public ResponseEntity<?> getUnreadLetters(@CurrentUser UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        List<MailBoxRes> mailBoxRes = mailBoxService.getUnreadLetters(userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/read")
    public ResponseEntity<?> getReadLetters(@CurrentUser UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        List<MailBoxRes> mailBoxRes = mailBoxService.getReadLetters(userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLetterDetails(@CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable Long id) {
        Long userId = userPrincipal.getId();
        Letter letter = mailBoxService.getLetterDetails(userId, id);

        MailBoxDetailsRes mailBoxDetailsRes = MailBoxDetailsRes.builder()
                .letterId(letter.getId())
                .senderNickname(letter.getSender().getNickname())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxDetailsRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
