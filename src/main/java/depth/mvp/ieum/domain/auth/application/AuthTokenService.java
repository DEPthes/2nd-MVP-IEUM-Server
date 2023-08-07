package depth.mvp.ieum.domain.auth.application;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import depth.mvp.ieum.domain.auth.domain.Token;
import depth.mvp.ieum.domain.auth.domain.repository.TokenRepository;
import depth.mvp.ieum.domain.auth.dto.AuthRes;
import depth.mvp.ieum.domain.auth.dto.RefreshTokenReq;
import depth.mvp.ieum.domain.auth.dto.TokenMapping;
import depth.mvp.ieum.global.DefaultAssert;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthTokenService {

    private final TokenRepository tokenRepository;
    private final CustomTokenProviderService customTokenProviderService;

    /**
     * 토큰 갱신
     * @param tokenRefreshRequest 토큰 갱신 request DTO
     * @return 토큰 갱신 response DTO
     */
    @Transactional
    public AuthRes refresh(RefreshTokenReq tokenRefreshRequest) {

        Optional<Token> token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken());
        DefaultAssert.isTrue(token.isPresent(), "다시 로그인 해주세요.");
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        TokenMapping tokenMapping;

        try {
            Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        } catch (ExpiredJwtException ex) {
            tokenMapping = customTokenProviderService.createToken(authentication);
            token.get().updateRefreshToken(tokenMapping.getRefreshToken());
        }

        Token updateToken = token.get().updateRefreshToken(tokenMapping.getRefreshToken());

        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(updateToken.getRefreshToken())
                .build();
    }

}
