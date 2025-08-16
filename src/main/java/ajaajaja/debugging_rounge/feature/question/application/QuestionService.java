package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequestDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequestDto;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundForDeleteException;
import ajaajaja.debugging_rounge.feature.question.domain.QuestionRepository;
import jakarta.transaction.Transactional;
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
        Question question = findQuestionByIdOrThrow(id);

        return QuestionDetailResponseDto.fromEntity(question);
    }

    @Transactional
    public void updateQuestion(Long id, QuestionUpdateRequestDto questionUpdateRequestDto) {
        Question question = findQuestionByIdOrThrow(id);

        question.update(questionUpdateRequestDto.getTitle(), questionUpdateRequestDto.getContent());
    }

    @Transactional
    public void deleteQuestion(Long id) {
        deleteQuestionOrThrow(id);
    }

    private void deleteQuestionOrThrow(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        questionRepository.delete(question);
    }

    private Question findQuestionByIdOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(QuestionNotFoundException::new);
    }
}