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
    @Pattern(regexp = "^(?!.*<img).*", message = "이미지 삽입은 허용되지 않습니다.")
    private String contents;

    private int envelopType;

    private boolean isRead;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // 우체통 기능 실행 시 프록시 문제 발생하므로 fetchType Eager로 변경,Hibernate.initialize()의 경우 로딩이 느려ㅓ 배제함
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;


    //public void setIsRead(boolean isRead) {
    //    this.isRead = isRead;
    //}
}
