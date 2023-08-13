package depth.mvp.ieum.domain.letter.presentation;

import depth.mvp.ieum.domain.letter.application.LetterService;
import depth.mvp.ieum.domain.letter.domain.Letter;
import depth.mvp.ieum.domain.letter.dto.LetterRes;
import depth.mvp.ieum.domain.letter.dto.LetterReq;
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

    @PostMapping("/send")
    public ResponseEntity<?> writeLetter(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody LetterReq letterReq) {

        Letter letter = letterService.writeLetter(userPrincipal, letterReq);

        LetterRes letterRes = LetterRes.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contents(letter.getContents())
                .envelopType(letter.getEnvelopType())
                .isRead(letter.isRead())
                .letterType(letter.getLetterType())
                .receiverId(letter.getReceiver().getId())
                .senderId(letter.getSender().getId())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(letterRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
