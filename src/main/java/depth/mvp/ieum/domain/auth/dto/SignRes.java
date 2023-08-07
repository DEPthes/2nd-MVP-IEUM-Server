package depth.mvp.ieum.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignRes {

    private Long id;

    private String nickname;

    private String email;

}
