package ajaajaja.debugging_rounge.feature.answer.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerCreateRequest;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.api.mapper.AnswerMapper;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.DeleteAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.GetAnswersQuery;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.UpdateAnswerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

public class AnswerController {

    private final CreateAnswerUseCase createAnswerUseCase;
    private final GetAnswersQuery getAnswersQuery;
    private final UpdateAnswerUseCase updateAnswerUseCase;
    private final DeleteAnswerUseCase deleteAnswerUseCase;
    private final AnswerMapper answerMapper;

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Long> createAnswer(
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid AnswerCreateRequest answerCreateRequest,
            @LoginUserId Long userId
    ) {
        Long answerId = createAnswerUseCase.createAnswer(answerCreateRequest.toDto(questionId, userId));
        return ResponseEntity
                .created(UriHelper.buildCreatedUri(answerId))
                .body(answerId);
    }

    @GetMapping("/questions/{questionId}/answers")
    public ResponseEntity<Page<AnswerDetailResponse>> getAnswersByQuestionId(
            @PathVariable("questionId") Long questionId,
            @LoginUserId(required = false) Long currentUserId,
            Pageable pageable
    ) {
        Page<AnswerDetailDto> answersPage = getAnswersQuery.getAllAnswerByQuestionId(questionId, pageable);

        Page<AnswerDetailResponse> answersResponsePage =
                answersPage.map(dto -> answerMapper.toResponse(dto, currentUserId));

        return ResponseEntity.ok(answersResponsePage);
    }

    @PutMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(
            @PathVariable("answerId")Long answerId,
            @RequestBody @Valid AnswerUpdateRequest answerUpdateRequest,
            @LoginUserId Long loginUserId
    ){
        updateAnswerUseCase.updateAnswer(answerMapper.toDto(answerUpdateRequest, answerId, loginUserId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable("answerId")Long answerId,
            @LoginUserId Long loginUserId
    ){
        deleteAnswerUseCase.deleteAnswer(answerId, loginUserId);

        return ResponseEntity.noContent().build();
    }
}
