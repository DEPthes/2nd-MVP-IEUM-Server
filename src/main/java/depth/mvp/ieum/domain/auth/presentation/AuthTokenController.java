package depth.mvp.ieum.domain.auth.presentation;

import depth.mvp.ieum.domain.auth.application.AuthTokenService;
import depth.mvp.ieum.domain.auth.dto.AuthRes;
import depth.mvp.ieum.domain.auth.dto.RefreshTokenReq;
import depth.mvp.ieum.global.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthTokenController {

    private final AuthTokenService authTokenService;

    //토큰 리프레시
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshTokenReq tokenRefreshRequest){

        AuthRes authRes = authTokenService.refresh(tokenRefreshRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
