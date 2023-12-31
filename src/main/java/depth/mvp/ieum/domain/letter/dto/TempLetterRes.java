package depth.mvp.ieum.domain.letter.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TempLetterRes {

    private Long letterId;
    private String title;
    private LocalDateTime modifiedAt;
}
