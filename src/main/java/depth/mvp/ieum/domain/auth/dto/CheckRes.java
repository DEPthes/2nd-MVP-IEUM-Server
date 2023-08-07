package depth.mvp.ieum.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckRes {

    private boolean available;
}
