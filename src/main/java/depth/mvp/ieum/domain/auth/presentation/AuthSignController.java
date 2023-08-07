package depth.mvp.ieum.domain.auth.presentation;

import depth.mvp.ieum.domain.auth.dto.SignUpReq;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

        AuthRes authRes = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody RefreshTokenReq refreshTokenReq){

        authSignService.signOut(refreshTokenReq);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("로그아웃 되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}