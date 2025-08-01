package ajaajaja.debuging_rounge.domain.question.controller;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionDetailResponseDto;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionUpdateRequestDto;
import ajaajaja.debuging_rounge.domain.question.service.QuestionService;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import ajaajaja.debuging_rounge.global.util.UriHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<QuestionListResponseDto>> findAllQuestions() {
        List<QuestionListResponseDto> questions = questionService.findAllQuestions();
        return ResponseEntity.ok(questions);
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