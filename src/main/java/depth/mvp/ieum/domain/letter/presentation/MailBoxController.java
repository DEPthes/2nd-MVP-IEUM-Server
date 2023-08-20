package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.MailBoxService;
import depth.mvp.ieum.domain.letter.dto.MailBoxDetailsRes;
import depth.mvp.ieum.domain.letter.dto.MailBoxRes;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mailbox")
public class MailBoxController {

    private final MailBoxService mailBoxService;

    @GetMapping
    public ResponseEntity<?> getUnreadLetters(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<MailBoxRes> mailBoxRes = mailBoxService.getUnreadLetters(userPrincipal.getId(), page, size);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/read")
    public ResponseEntity<?> getReadLetters(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<MailBoxRes> mailBoxRes = mailBoxService.getReadLetters(userPrincipal.getId(), page, size);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLetterDetails(@CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable Long id) {
        MailBoxDetailsRes mailBoxDetailsRes = mailBoxService.getLetterDetails(userPrincipal.getId(), id);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(mailBoxDetailsRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
