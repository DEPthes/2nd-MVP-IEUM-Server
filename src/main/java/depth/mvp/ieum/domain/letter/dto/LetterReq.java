package depth.mvp.ieum.domain.letter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterReq {

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    private int envelopType;
}
