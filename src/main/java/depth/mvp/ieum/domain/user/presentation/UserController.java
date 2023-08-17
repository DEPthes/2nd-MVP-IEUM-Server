package depth.mvp.ieum.domain.user.presentation;

import depth.mvp.ieum.domain.user.application.UserService;
import depth.mvp.ieum.domain.user.dto.VerfiyRes;
import depth.mvp.ieum.global.config.security.token.CurrentUser;
import depth.mvp.ieum.global.config.security.token.UserPrincipal;
import depth.mvp.ieum.global.payload.ApiResponse;
import depth.mvp.ieum.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @DeleteMapping("/leave")
    public ResponseEntity<?> deleteUser(@CurrentUser UserPrincipal userPrincipal) {

        userService.deleteUser(userPrincipal);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("계정이 삭제되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@CurrentUser UserPrincipal userPrincipal) {

        Long userId = userService.verifyUser(userPrincipal);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(VerfiyRes.builder().id(userId).build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
