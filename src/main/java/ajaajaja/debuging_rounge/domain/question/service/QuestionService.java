package ajaajaja.debuging_rounge.domain.question.service;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionDetailResponseDto;
import ajaajaja.debuging_rounge.domain.question.entity.Question;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import ajaajaja.debuging_rounge.domain.question.repository.QuestionRepository;
import ajaajaja.debuging_rounge.domain.question.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long createQuestion(QuestionCreateRequestDto questionCreateRequestDto) {
        Question question = questionCreateRequestDto.toEntity(questionCreateRequestDto);

        Question savedQuestion = questionRepository.save(question);

        return savedQuestion.getId();
    }

    public QuestionDetailResponseDto findQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(QuestionNotFoundException::new);

        return QuestionDetailResponseDto.fromEntity(question);
    }
}
