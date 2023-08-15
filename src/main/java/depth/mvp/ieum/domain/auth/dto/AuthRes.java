package depth.mvp.ieum.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthRes {

    private String accessToken;

//    private String refreshToken;

    @Builder
    public AuthRes(String accessToken) {
        this.accessToken = accessToken;
    }
}
