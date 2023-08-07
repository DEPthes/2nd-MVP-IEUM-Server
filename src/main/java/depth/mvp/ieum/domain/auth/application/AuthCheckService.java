package depth.mvp.ieum.domain.auth.application;

import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCheckService {

    private final UserRepository userRepository;

    /**
     * 이메일 중복 체크
     * @param email 체크할 이메일
     * @return 중복되지 않으면 true, 중복이면 false
     */
    public boolean emailCheck(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isEmpty();
    }

    /**
     * 닉네임 중복 체크
     * @param nickname 체크할 닉네임
     * @return 중복되지 않으면 true, 중복이면 false
     */
    public boolean nicknameCheck(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isEmpty();
    }
}
