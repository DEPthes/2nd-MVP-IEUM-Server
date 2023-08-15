package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.LetterService;
import depth.mvp.ieum.domain.letter.application.TempLetterService;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.letter.dto.TempLetterRes;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/letter")
public class LetterController {

    private final LetterService letterService;
    private final TempLetterService tempLetterService;

    @PostMapping("/send")
    public ResponseEntity<?> writeLetter(@CurrentUser UserPrincipal userPrincipal,
                                         @Valid @RequestBody LetterReq letterReq) {
        LetterRes letterRes = letterService.writeLetter(userPrincipal.getId(), letterReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 편지 임시저장
    @PostMapping("/temp")
    public ResponseEntity<?> writeTempLetter(@CurrentUser UserPrincipal userPrincipal,
                                             @Valid @RequestBody LetterReq letterReq) {
        LetterRes letterRes = tempLetterService.writeTempLetter(userPrincipal.getId(), letterReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 임시저장 리스트로 조회
    // 신규 작성
    @GetMapping("/temp-new")
    public ResponseEntity<?> getNewTempLetters(@CurrentUser UserPrincipal userPrincipal) {
        List<TempLetterRes> tempLetterRes = tempLetterService.getNewTempLetters(userPrincipal.getId());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(tempLetterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 답장
    @GetMapping("/temp-reply")
    public ResponseEntity<?> getReplyTempLetters(@CurrentUser UserPrincipal userPrincipal) {
        List<TempLetterRes> tempLetterRes = tempLetterService.getReplyTempLetters(userPrincipal.getId());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(tempLetterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 임시 저장된 편지 불러오기
    @GetMapping("/temp/{letterId}")
    public ResponseEntity<?> getTempLetter(@CurrentUser UserPrincipal userPrincipal,
                                           @PathVariable Long letterId) {
        LetterRes letterRes = tempLetterService.getTempLetter(letterId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
