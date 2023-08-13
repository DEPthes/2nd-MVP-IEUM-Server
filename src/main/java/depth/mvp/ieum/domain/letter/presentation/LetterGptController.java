package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.gpt.application.ChatGptService;
import depth.mvp.ieum.domain.letter.application.LetterGptService;
import depth.mvp.ieum.domain.letter.dto.LetterCheckReq;
import depth.mvp.ieum.domain.letter.dto.LetterCheckRes;
import depth.mvp.ieum.domain.letter.dto.LetterSendReq;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import depth.mvp.ieum.global.payload.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/letter")
public class LetterGptController {

    private final LetterGptService letterGptService;

    // gpt에게 편지 발송
    @PostMapping("/send-gpt")
    public ResponseEntity<?> writeLetterForGpt(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody LetterSendReq letterReq) {

        letterGptService.writeLetterForGpt(userPrincipal, letterReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("GPT에게 편지가 발송되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // gpt에게 편지 검사받기
    @GetMapping("/check-gpt")
    public ResponseEntity<?> checkLetter(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody LetterCheckReq letterCheckReq) {

        String checkResponse = letterGptService.checkLetter(userPrincipal, letterCheckReq);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(LetterCheckRes.builder().prohibition(Integer.parseInt(checkResponse)).build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
