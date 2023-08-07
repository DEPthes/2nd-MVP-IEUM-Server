package depth.mvp.ieum.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReq {

    @NotBlank(message = "Refersh Token을 입력해야 합니다.")
    private String refreshToken;

}
