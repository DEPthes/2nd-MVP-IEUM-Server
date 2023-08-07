package depth.mvp.ieum.domain.verify.presentation;

import depth.mvp.ieum.domain.verify.application.VerifyService;
import depth.mvp.ieum.domain.verify.dto.SendEmailReq;
import depth.mvp.ieum.global.payload.ApiResponse;
import depth.mvp.ieum.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {

    private final VerifyService verifyService;

    // 인증 코드 발송
    @PostMapping("/send")
    public ResponseEntity<?> sendVerifyCode(
            @RequestBody SendEmailReq sendEmailReq) {

        verifyService.sendVerifyCode(sendEmailReq.getEmail());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("인증코드가 발급되었습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 인증 코드 확인
    @DeleteMapping("/check/{code}")
    public ResponseEntity<?> checkVerify(
            @PathVariable(value = "code") String code) {

        verifyService.checkVerify(code);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("성공적으로 인증되었어요!").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
