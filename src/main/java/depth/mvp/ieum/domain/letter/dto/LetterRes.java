package depth.mvp.ieum.domain.letter.dto;


import depth.mvp.ieum.domain.letter.domain.LetterType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LetterRes {

    private Long id;

    private String title;

    private String contents;

    private int envelopType;

    private boolean isRead;

    private LetterType letterType;

    private Long senderId;

    private Long receiverId;
}
