package depth.mvp.ieum.domain.letter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailBoxDetailsRes {

    private Long letterId;

    private String senderNickname;

    private String title;

    private String contents;

    private int envelopType;

    private boolean isRead;


}
