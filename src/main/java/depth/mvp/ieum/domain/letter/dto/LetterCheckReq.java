package depth.mvp.ieum.domain.letter.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterCheckReq {

    @NotBlank(message = "제목을 입력해야 합니다.")
    @Size(max = 28, message = "제목은 최대 28자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
    @Size(max = 3500, message = "내용은 최대 3500자까지 입력 가능합니다.")
    @Lob
    private String contents;
}
