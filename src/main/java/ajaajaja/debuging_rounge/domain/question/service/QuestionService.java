package ajaajaja.debuging_rounge.domain.question.service;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionDetailResponseDto;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionUpdateRequestDto;
import ajaajaja.debuging_rounge.domain.question.entity.Question;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import ajaajaja.debuging_rounge.domain.question.exception.QuestionNotFoundForDeleteException;
import ajaajaja.debuging_rounge.domain.question.repository.QuestionRepository;
import ajaajaja.debuging_rounge.domain.question.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long createQuestion(QuestionCreateRequestDto questionCreateRequestDto) {
        Question question = questionCreateRequestDto.toEntity(questionCreateRequestDto);

        Question savedQuestion = questionRepository.save(question);

        return savedQuestion.getId();
    }

    public Page<QuestionListResponseDto> findQuestionsWithPreview(Pageable pageable) {
        return questionRepository.findQuestionsWithPreview(pageable);
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