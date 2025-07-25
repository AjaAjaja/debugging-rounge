package ajaajaja.debuging_rounge.domain.question.service;

import ajaajaja.debuging_rounge.domain.question.entity.Question;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import ajaajaja.debuging_rounge.domain.question.repository.QuestionRepository;
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
}
