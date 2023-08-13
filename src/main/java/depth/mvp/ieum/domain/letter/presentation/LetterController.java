package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.LetterService;
import depth.mvp.ieum.domain.letter.application.TempLetterService;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        LetterRes letterRes = tempLetterService.writeTempLetter(userPrincipal, letterReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}
