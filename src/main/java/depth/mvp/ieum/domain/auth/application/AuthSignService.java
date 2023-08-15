package depth.mvp.ieum.domain.auth.application;

import depth.mvp.ieum.domain.auth.domain.Token;
import depth.mvp.ieum.domain.auth.domain.repository.TokenRepository;
import depth.mvp.ieum.domain.auth.dto.*;
import depth.mvp.ieum.domain.user.domain.Role;
import depth.mvp.ieum.domain.user.domain.User;
import depth.mvp.ieum.domain.user.domain.repository.UserRepository;
import depth.mvp.ieum.global.DefaultAssert;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthSignService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final CustomTokenProviderService customTokenProviderService;

    /**
     * 회원가입
     * @param signUpReq 회원가입 request DTO
     * @return 회원가입 response DTO
     */
    @Transactional
    public User signUp(SignUpReq signUpReq) {

        DefaultAssert.isTrue(!userRepository.existsByEmail(signUpReq.getEmail()), "해당 이메일이 존재합니다.");
        DefaultAssert.isTrue(!userRepository.existsByNickname(signUpReq.getNickname()), "이미 존재하는 닉네임이에요.");

        // 유저 객체 생성
        User user = User.builder()
                .nickname(signUpReq.getNickname())
                .email(signUpReq.getEmail())
                .password(passwordEncoder.encode(signUpReq.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return user;
    }


    /**
     * 로그인
     * @param signInReq 로그인 request DTO
     * @return 로그인 response DTO
     */
    @Transactional
    public TokenMapping signIn(SignInReq signInReq){

        // DB에서 유저 가져오기
        Optional<User> user = userRepository.findByEmail(signInReq.getEmail());
        DefaultAssert.isTrue(user.isPresent(), "해당 이메일을 가진 유저가 없습니다.");
        User findMember = user.get();

        // 비밀번호 맞는지 체크
        boolean passwordCheck = passwordEncoder.matches(signInReq.getPassword(), findMember.getPassword());
        DefaultAssert.isTrue(passwordCheck, "비밀번호가 일치하지 않습니다.");

        // 인증 객체 만들기
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInReq.getEmail(),
                        signInReq.getPassword()
                )
        );

        // 위에서 만든 인증 객체로 jwt 만들기
        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);

        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();

        tokenRepository.save(token);

        return tokenMapping;
    }

    /**
     * 로그아웃
     * @param refreshToken 리프레시 토큰
     */
    @Transactional
    public void signOut(String refreshToken){

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "이미 로그아웃 되었습니다");

        tokenRepository.delete(token.get());
    }

    /**
     * 비밀번호 재설정
     * @param changePwReq 비밀번호 재설정 request DTO
     */
    @Transactional
    public void changePassword(ChangePwReq changePwReq) {

        Optional<User> user = userRepository.findByEmail(changePwReq.getEmail());
        DefaultAssert.isTrue(user.isPresent(), "해당 이메일을 가지고 있는 유저가 없습니다.");
        User findUser = user.get();

        DefaultAssert.isTrue(changePwReq.getNewPassword().equals(changePwReq.getRenewPassword()), "비밀번호를 다시 확인해주세요.");

        // 새로운 비밀번호 인코딩해서 업데이트
        findUser.updatePassword(passwordEncoder.encode(changePwReq.getNewPassword()));
    }
}
