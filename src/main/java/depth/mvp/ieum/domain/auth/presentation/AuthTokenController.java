package depth.mvp.ieum.domain.auth.presentation;

import depth.mvp.ieum.domain.auth.application.AuthTokenService;
import depth.mvp.ieum.domain.auth.dto.AuthRes;
import depth.mvp.ieum.domain.auth.dto.RefreshTokenReq;
import depth.mvp.ieum.global.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthTokenController {

    private final AuthTokenService authTokenService;

    //토큰 리프레시
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue("refreshToken") String refreshToken){

        AuthRes authRes = authTokenService.refresh(refreshToken);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
