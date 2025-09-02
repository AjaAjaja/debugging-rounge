package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.common.security.annotation.CurrentUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequest;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionMapper;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionCreateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionUpdateDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final GetQuestionDetailQuery getQuestionDetailQuery;
    private final GetQuestionListWithPreviewQuery getQuestionListWithPreviewQuery;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;
    private final QuestionMapper questionMapper;

    @PostMapping
    public ResponseEntity<Long> createQuestion(
            @CurrentUserId Long userId,
            @RequestBody @Valid QuestionCreateRequest questionCreateRequest) {

        QuestionCreateDto questionCreateDto =
                QuestionCreateDto.of(questionCreateRequest.title(), questionCreateRequest.content(), userId);
        Long questionId = createQuestionUseCase.createQuestion(questionCreateDto);

        return ResponseEntity
                .created(UriHelper.buildCreatedUri(questionId))
                .body(questionId);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> findQuestion(
            @PathVariable("questionId") Long questionId,
            @CurrentUserId(required = false) Long userId) {
        QuestionDetailDto questionDetailDto = getQuestionDetailQuery.findQuestionById(questionId);
        QuestionDetailResponse questionDetailResponse = questionMapper.toResponse(questionDetailDto);
        if (userId != null) {
            questionDetailResponse.addLoginUserId(userId);
        }
        return ResponseEntity.ok(questionDetailResponse);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionListResponse>> findQuestionsWithPreview(Pageable pageable) {
        Page<QuestionListDto> questionListDtos = getQuestionListWithPreviewQuery.findQuestionsWithPreview(pageable);
        return ResponseEntity.ok(questionListDtos.map(questionMapper::toResponse));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionUpdateRequest questionUpdateRequest,
            @CurrentUserId Long loginUserId
    ){
        QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.of(
                questionId, questionUpdateRequest.title(), questionUpdateRequest.content(), loginUserId);
        updateQuestionUseCase.updateQuestion(questionUpdateDto);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("questionId") Long questionId,
            @CurrentUserId Long loginUserId){
        deleteQuestionUseCase.deleteQuestion(questionId, loginUserId);

        return ResponseEntity.noContent().build();
    }
}