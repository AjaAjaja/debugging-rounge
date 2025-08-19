package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.common.security.CurrentUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequestDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequestDto;
import ajaajaja.debugging_rounge.feature.question.application.QuestionService;
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

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Long> createQuestion(
            @CurrentUserId Long userId,
            @RequestBody @Valid QuestionCreateRequestDto questionCreateRequestDto) {
        Long questionId = questionService.createQuestion(questionCreateRequestDto, userId);

        return ResponseEntity
                .created(UriHelper.buildCreatedUri(questionId))
                .body(questionId);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionListResponseDto>> findQuestionsWithPreview(Pageable pageable) {
        Page<QuestionListResponseDto> questions = questionService.findQuestionsWithPreview(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponseDto> findQuestion(
            @PathVariable("questionId") Long questionId,
            @CurrentUserId(required = false) Long userId) {
        return ResponseEntity.ok(questionService.findQuestionById(questionId, userId));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionUpdateRequestDto questionUpdateRequestDto
    ){
        questionService.updateQuestion(questionId, questionUpdateRequestDto);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("questionId") Long questionId){
        questionService.deleteQuestion(questionId);

        return ResponseEntity.noContent().build();
    }
}