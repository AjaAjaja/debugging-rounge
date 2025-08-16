package ajaajaja.debuging_rounge.domain.question.controller;

import ajaajaja.debuging_rounge.domain.question.service.QuestionService;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Long> createQuestion(@RequestBody @Valid QuestionCreateRequestDto questionCreateRequestDto) {
        Long questionId = questionService.createQuestion(questionCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionId);
    }
}
