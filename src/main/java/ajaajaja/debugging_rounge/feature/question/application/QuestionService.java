package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequestDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequestDto;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.domain.QuestionRepository;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundForDeleteException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Long createQuestion(QuestionCreateRequestDto questionCreateRequestDto, Long userId) {
        Question question = questionCreateRequestDto.toEntity(questionCreateRequestDto, userId);

        Question savedQuestion = questionRepository.save(question);

        return savedQuestion.getId();
    }

    @Transactional(readOnly = true)
    public Page<QuestionListResponseDto> findQuestionsWithPreview(Pageable pageable) {
        return questionRepository.findQuestionsWithPreview(pageable);
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponseDto findQuestionById(Long questionId, Long userId) {
        QuestionDetailResponseDto questionDetailResponseDto =
                questionRepository.findQuestionDetailById(questionId).orElseThrow(QuestionNotFoundException::new);
        if (userId != null) {
            questionDetailResponseDto.addLoginUserId(userId);
        }

        return questionDetailResponseDto;
    }

    @Transactional
    public void updateQuestion(Long questionId, QuestionUpdateRequestDto questionUpdateRequestDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);

        question.update(questionUpdateRequestDto.getTitle(), questionUpdateRequestDto.getContent());
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        deleteQuestionOrThrow(questionId);
    }

    private void deleteQuestionOrThrow(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        questionRepository.delete(question);
    }

}