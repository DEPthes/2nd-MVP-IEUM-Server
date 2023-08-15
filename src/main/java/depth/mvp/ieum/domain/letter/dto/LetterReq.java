package depth.mvp.ieum.domain.letter.dto;

import depth.mvp.ieum.domain.letter.domain.LetterType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterReq {

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 28, message = "제목은 최대 28자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Size(max = 3500, message = "내용은 최대 3500자까지 입력 가능합니다.")
    @Lob
    private String contents;

    private int envelopType = 1;

    private Long originalLetterId; // (편지 답장 시) 답장할 편지의 id

    // (임시 저장된 편지 불러올 경우 사용)
    private LetterType letterType;
    private Long letterId;

}
