package depth.mvp.ieum.domain.auth.presentation;

import depth.mvp.ieum.domain.auth.application.AuthCheckService;
import depth.mvp.ieum.domain.auth.dto.CheckRes;
import depth.mvp.ieum.domain.gpt.application.ChatGptService;
import depth.mvp.ieum.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthCheckController {

    private final AuthCheckService authCheckService;
    private final ChatGptService chatGptService;

    // 이메일 중복 체크
    @GetMapping("/email/{email}")
    public ResponseEntity<?> emailCheck(
            @PathVariable(value = "email") String email) {

        CheckRes checkRes = CheckRes.builder()
                .available(authCheckService.emailCheck(email))
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(checkRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 중복 체크
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<?> nicknameCheck(
            @PathVariable(value = "nickname") String nickname) {

        CheckRes checkRes = CheckRes.builder()
                .available(authCheckService.nicknameCheck(nickname))
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(checkRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 추천 받기
    @GetMapping("/nickname")
    public ResponseEntity<?> recommendNickname() {

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authCheckService.recommendNickname())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
