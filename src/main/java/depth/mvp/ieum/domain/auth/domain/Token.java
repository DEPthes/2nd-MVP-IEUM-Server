package depth.mvp.ieum.domain.auth.domain;

import io.micrometer.observation.transport.ResponseContext;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import depth.mvp.ieum.domain.common.BaseEntity;
import org.springframework.http.ResponseCookie;

import java.net.ResponseCache;

@Getter
@Table(name="token")
@Entity
public class Token extends BaseEntity {

    @Id
    @Column(name = "user_email", length = 767 , nullable = false)
    private String userEmail;

    @Column(name = "refresh_token", length = 767 , nullable = false)
    private String refreshToken;

    public Token(){}

    // 쿠키 생성 메서드
    public ResponseCookie generateCookie() {
        return ResponseCookie.from("refreshToken", this.refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();
    }

    // 쿠키 (로그아웃 용) 생성 메서드
    public ResponseCookie generateSignOutCookie() {
        return ResponseCookie.from("refreshToken", "")
                .maxAge(1)
                .build();
    }

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public Token(String userEmail, String refreshToken) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}
