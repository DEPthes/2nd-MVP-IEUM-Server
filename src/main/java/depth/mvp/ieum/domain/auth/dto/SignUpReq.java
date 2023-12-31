package depth.mvp.ieum.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpReq {

    @NotBlank(message = "이름을 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z가-힣]).{3,10}$", message = "닉네임은 3~10자의 한글과 영문 조합이어야 합니다.")
    private String nickname;

    @NotBlank
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!/@/^])[A-Za-z\\d!/@/^]{8,12}$",
            message = "비밀번호는 영문, 숫자, 특수문자(!/@/^)를 모두 포함한 8~12자여야 합니다.")
    private String password;

}
