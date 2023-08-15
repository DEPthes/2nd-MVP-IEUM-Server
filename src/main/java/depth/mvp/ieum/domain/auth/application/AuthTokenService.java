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
     * @param refreshToken 토큰을 재발급 받기 위한 리프레시 토큰
     * @return 토큰 갱신 response DTO
     */
    @Transactional
    public AuthRes refresh(String refreshToken) {

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "다시 로그인 해주세요.");
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        TokenMapping tokenMapping;

        Long expirationTime = customTokenProviderService.getExpiration(refreshToken);

        if(expirationTime > 0){
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        }else{
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.get().updateRefreshToken(tokenMapping.getRefreshToken());
        tokenRepository.save(updateToken);

        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .build();
    }

    // 유저 이메일로 토큰 찾기
    public Token getTokenByEmail(String email) {

        Optional<Token> token = tokenRepository.findByUserEmail(email);
        DefaultAssert.isTrue(token.isPresent(), "이메일에 해당하는 토큰이 없습니다.");

        return token.get();
    }

    // 리프레시 토큰으로 토큰 찾기
    public Token getTokenByRefreshToken(String refreshToken) {

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "리프레시 토큰에 해당하는 토큰이 없습니다.");

        return token.get();
    }
}
