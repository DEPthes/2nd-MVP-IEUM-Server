package depth.mvp.ieum.domain.letter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterReplyReq {

    private Long originalLetterId; // 답장할 편지의 id

    // 편지 작성 시 필요한 컬럼
    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    private int envelopType;

}
