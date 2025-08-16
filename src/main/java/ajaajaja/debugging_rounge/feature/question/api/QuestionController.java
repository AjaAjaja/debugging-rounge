package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequestDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequestDto;
import ajaajaja.debugging_rounge.feature.question.application.QuestionService;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Long> createQuestion(@RequestBody @Valid QuestionCreateRequestDto questionCreateRequestDto) {
        Long questionId = questionService.createQuestion(questionCreateRequestDto);

        return ResponseEntity
                .created(UriHelper.buildCreatedUri(questionId))
                .body(questionId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDetailResponseDto> findQuestion(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.findQuestionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("id") Long id,
            @RequestBody @Valid QuestionUpdateRequestDto questionUpdateRequestDto
    ){
        questionService.updateQuestion(id, questionUpdateRequestDto);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long id){
        questionService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }
}