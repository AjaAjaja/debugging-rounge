package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.answer.api.mapper.AnswerMapper;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequest;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionWithAnswerResponse;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionResponseMapper;
import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionCreateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionUpdateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final GetQuestionWithAnswersQuery getQuestionWithAnswersQuery;
    private final GetQuestionListWithPreviewQuery getQuestionListWithPreviewQuery;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;
    private final QuestionResponseMapper questionResponseMapper;

    @PostMapping
    public ResponseEntity<Long> createQuestion(
            @LoginUserId Long userId,
            @RequestBody @Valid QuestionCreateRequest questionCreateRequest) {

        QuestionCreateDto questionCreateDto =
                QuestionCreateDto.of(questionCreateRequest.title(), questionCreateRequest.content(), userId);
        Long questionId = createQuestionUseCase.createQuestion(questionCreateDto);

        return ResponseEntity
                .created(UriHelper.buildCreatedUri(questionId))
                .body(questionId);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionWithAnswerResponse> findQuestionWithAnswers(
            @PathVariable("questionId") Long questionId,
            @LoginUserId(required = false) Long loginUserId,
            Pageable answerPageable) {

        QuestionWithAnswersDto questionWithAnswersDto =
                getQuestionWithAnswersQuery.getQuestionWithAnswers(questionId, loginUserId, answerPageable);

        QuestionWithAnswerResponse questionWithAnswerResponse =
                questionResponseMapper.toQuestionWithAnswersResponse(questionWithAnswersDto, loginUserId);

        return ResponseEntity.ok(questionWithAnswerResponse);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionListResponse>> findQuestionsWithPreview(
            @RequestParam(name = "order", defaultValue = "LATEST") QuestionOrder order,
            @PageableDefault(sort = {"createdDate", "id"}, direction = Sort.Direction.DESC)
            Pageable pageable, HttpServletRequest request) {
        Page<QuestionListDto> questionListDtos = getQuestionListWithPreviewQuery.getQuestionsWithPreview(pageable, order);
        return ResponseEntity.ok(questionListDtos.map(questionResponseMapper::toQuestionListResponse));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionUpdateRequest questionUpdateRequest,
            @LoginUserId Long loginUserId
    ){
        QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.of(
                questionId, questionUpdateRequest.title(), questionUpdateRequest.content(), loginUserId);
        updateQuestionUseCase.updateQuestion(questionUpdateDto);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("questionId") Long questionId,
            @LoginUserId Long loginUserId){
        deleteQuestionUseCase.deleteQuestion(questionId, loginUserId);

        return ResponseEntity.noContent().build();
    }
}