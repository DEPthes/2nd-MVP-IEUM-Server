package depth.mvp.ieum.domain.letter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterReplyReq {

    private Long originalLetterId; // 답장할 편지의 id

    // 편지 작성 시 필요한 컬럼
    @NotBlank(message = "제목을 입력해야 합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Pattern(regexp = "^(?!.*<img).*", message = "이미지 삽입은 허용되지 않습니다.")
    private String contents;

    private int envelopType;

}
