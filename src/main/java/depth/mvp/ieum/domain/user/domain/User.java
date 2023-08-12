package depth.mvp.ieum.domain.user.domain;

import depth.mvp.ieum.domain.common.BaseEntity;
import depth.mvp.ieum.domain.letter.domain.Letter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Email(message = "이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일을 입력해야 합니다.")
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "sender")
    private List<Letter> sentLetters;

    @OneToMany(mappedBy = "receiver")
    private List<Letter> receivedLetters;

    // update 메서드
    public void updatePassword(String password) {
        this.password = password;
    }
}
