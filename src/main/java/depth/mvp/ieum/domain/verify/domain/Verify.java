package depth.mvp.ieum.domain.verify.domain;

import depth.mvp.ieum.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Verify extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verify_id")
    private Long id;

    private String code;

    // 인증에 사용할 이메일
    private String email;

    // 인증 코드 유효시간 3분
    @Builder.Default
    private LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(3);

    public boolean checkExpiration(LocalDateTime now) {
        return now.isBefore(expirationTime);
    }
}
