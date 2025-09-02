package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionCreateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionUpdateDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import ajaajaja.debugging_rounge.feature.question.application.port.out.DeleteQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.SaveQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionDeleteForbiddenException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundForDeleteException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionUpdateForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionFacade implements
        CreateQuestionUseCase,
        GetQuestionDetailQuery, GetQuestionListWithPreviewQuery,
        UpdateQuestionUseCase,
        DeleteQuestionUseCase {


    private final SaveQuestionPort saveQuestionPort;
    private final LoadQuestionPort loadQuestionPort;
    private final DeleteQuestionPort deleteQuestionPort;

    @Override
    @Transactional
    public Long createQuestion(QuestionCreateDto questionCreateDto) {
        Question question = questionCreateDto.toEntity();

        Question savedQuestion = saveQuestionPort.save(question);

        return savedQuestion.getId();
    }

    @Override
    public Page<QuestionListDto> findQuestionsWithPreview(Pageable pageable) {
        return loadQuestionPort.findQuestionsWithPreview(pageable);
    }

    @Override
    public QuestionDetailDto findQuestionById(Long questionId) {
        return loadQuestionPort.findQuestionDetailById(questionId).orElseThrow(QuestionNotFoundException::new);
    }

    @Override
    @Transactional
    public void updateQuestion(QuestionUpdateDto questionUpdateDto) {
        Question question = loadQuestionPort.findById(questionUpdateDto.id())
                .orElseThrow(QuestionNotFoundException::new);

        validateAuthor(question.getUserId(), questionUpdateDto.loginUserId(), QuestionUpdateForbiddenException::new);

        if (hasChanges(question, questionUpdateDto)) {
            question.update(questionUpdateDto.title(), questionUpdateDto.content());
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, Long loginUserId) {
        Question question = loadQuestionPort.findById(questionId)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        validateAuthor(question.getUserId(), loginUserId, QuestionDeleteForbiddenException::new);

        deleteQuestionPort.deleteById(question.getId());
    }

    private void validateAuthor(Long authorId, Long loginUserId, Supplier<? extends CustomAuthorizationException> ex) {
        if (!Objects.equals(authorId, loginUserId)) {
            throw ex.get();
        }
    }

    private boolean hasChanges(Question question, QuestionUpdateDto questionUpdateDto) {
        return !Objects.equals(question.getTitle(), questionUpdateDto.title())
                || !Objects.equals(question.getContent(), questionUpdateDto.content());
    }

}