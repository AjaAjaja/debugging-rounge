package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.common.exception.auth.CustomAuthorizationException;
import ajaajaja.debugging_rounge.common.exception.auth.QuestionDeleteForbiddenException;
import ajaajaja.debugging_rounge.common.exception.auth.QuestionUpdateForbiddenExceptionCustom;
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

import java.util.Objects;
import java.util.function.Supplier;

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
    public void updateQuestion(Long questionId, QuestionUpdateRequestDto questionUpdateRequestDto, Long loginUserId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        validateAuthor(question.getUserId(), loginUserId, QuestionUpdateForbiddenExceptionCustom::new);

        question.update(questionUpdateRequestDto.getTitle(), questionUpdateRequestDto.getContent());
    }

    @Transactional
    public void deleteQuestion(Long questionId, Long loginUserId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        validateAuthor(question.getUserId(), loginUserId, QuestionDeleteForbiddenException::new);

        questionRepository.delete(question);
    }

    private void validateAuthor(Long authorId, Long loginUserId, Supplier<? extends CustomAuthorizationException> ex) {
        if (!Objects.equals(authorId, loginUserId)) {
            throw ex.get();
        }
    }

}