package depth.mvp.ieum.domain.auth.presentation;

import depth.mvp.ieum.domain.auth.application.AuthTokenService;
import depth.mvp.ieum.domain.auth.domain.Token;
import depth.mvp.ieum.domain.auth.dto.SignUpReq;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import depth.mvp.ieum.domain.auth.application.AuthSignService;
import depth.mvp.ieum.domain.auth.dto.*;
import depth.mvp.ieum.global.payload.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthSignController {

    private final AuthSignService authSignService;
    private final AuthTokenService authTokenService;

    public static final String SET_COOKIE = "Set-Cookie";

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpCustomer(
            @Valid @RequestBody SignUpReq signUpReq) {

        User user = authSignService.signUp(signUpReq);

        SignRes signRes = SignRes.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(signRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //로그인
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Valid @RequestBody SignInReq signInReq) {

        TokenMapping tokenMapping = authSignService.signIn(signInReq);
        Token token = authTokenService.getTokenByEmail(signInReq.getEmail());

        AuthRes authRes = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok()
                .header(SET_COOKIE, token.generateCookie().toString())
                .body(apiResponse);
    }

    //로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @CookieValue("refreshToken") String refreshToken) {

        Token token = authTokenService.getTokenByRefreshToken(refreshToken);
        authSignService.signOut(refreshToken);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("로그아웃 되었습니다.").build())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.generateSignOutCookie().toString())
                .body(apiResponse);
    }

    // 비밀번호 재설정
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePwReq changePwReq) {

        authSignService.changePassword(changePwReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("비밀번호가 변경되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}