package ajaajaja.debugging_rounge.feature.answer.api;

import ajaajaja.debugging_rounge.common.security.annotation.CurrentUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerCreateRequest;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class AnswerController {

    private final CreateAnswerUseCase createAnswerUseCase;

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Long> createAnswer(
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid AnswerCreateRequest answerCreateRequest,
            @CurrentUserId Long userId
    ) {
        Long answerId = createAnswerUseCase.createAnswer(answerCreateRequest.toDto(questionId, userId));
        return ResponseEntity
                .created(UriHelper.buildCreatedUri(answerId))
                .body(answerId);
    }
}
