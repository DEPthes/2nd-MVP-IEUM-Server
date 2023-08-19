package depth.mvp.ieum.domain.letter.domain;

import depth.mvp.ieum.domain.common.BaseEntity;
import depth.mvp.ieum.domain.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Letter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long id;

    @NotBlank(message = "제목을 입력해야 합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해야 합니다.")
//    @Pattern(regexp = "^(?!.*<img).*", message = "이미지 삽입은 허용되지 않습니다.")
    @Lob
    private String contents;

    private int envelopType = 1;

    private boolean isRead;

    private boolean isGPT;   // 챗지피티가 보낸 편지, 챗지피티한테 보낸 편지일 경우 true

    @Enumerated(EnumType.STRING)
    private LetterType letterType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;


    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
    public void updateTempLetterToLetter(Long id, String title, String contents, int envelopType,
                                         boolean isRead, LetterType letterType, User sender, User receiver) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.envelopType = envelopType;
        this.isRead = isRead;
        this.letterType = letterType;
        this.sender = sender;
        this.receiver = receiver;
    }
}
