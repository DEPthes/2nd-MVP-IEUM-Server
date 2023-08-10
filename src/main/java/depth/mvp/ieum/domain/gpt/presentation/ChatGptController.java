package depth.mvp.ieum.domain.gpt.presentation;

import depth.mvp.ieum.domain.gpt.application.ChatGptService;
import depth.mvp.ieum.domain.gpt.dto.ChatGptRes;
import depth.mvp.ieum.domain.gpt.dto.QuestionReq;
import depth.mvp.ieum.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/auth/chat-gpt")
@RestController
public class ChatGptController {

    private final ChatGptService chatGptService;

    // gpt api 테스트하는 api입니다. 추후 삭제 예정입니다.
    @PostMapping("/question")
    public ResponseEntity<?> sendQuestion(
            @RequestBody QuestionReq questionRequest) {

        ChatGptRes chatGptResponse = chatGptService.askQuestion(questionRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(chatGptResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}