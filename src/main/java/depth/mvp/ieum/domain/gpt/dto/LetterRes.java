package depth.mvp.ieum.domain.gpt.dto;

import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LetterRes {

    @Lob
    private String data;
}
