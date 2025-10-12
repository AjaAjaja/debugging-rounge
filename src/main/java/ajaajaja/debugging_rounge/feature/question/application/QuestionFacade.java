package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.common.security.validator.OwnerShipValidator;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.question.application.dto.*;
import ajaajaja.debugging_rounge.feature.question.application.mapper.QuestionWithAnswersMapper;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionFacade implements
        CreateQuestionUseCase,
        GetQuestionDetailQuery, GetQuestionWithAnswersQuery,
        GetQuestionListWithPreviewQuery,
        UpdateQuestionUseCase,
        DeleteQuestionUseCase {


    private final SaveQuestionPort saveQuestionPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerPort loadAnswerPort;
    private final DeleteQuestionPort deleteQuestionPort;
    private final OwnerShipValidator ownerShipValidator;
    private final QuestionWithAnswersMapper mapper;

    @Override
    @Transactional
    public Long createQuestion(QuestionCreateDto questionCreateDto) {
        Question question = questionCreateDto.toEntity();

        Question savedQuestion = saveQuestionPort.save(question);

        return savedQuestion.getId();
    }

    @Override
    public QuestionDetailDto findQuestionById(Long questionId) {
        return null;
    }

    @Override
    public Page<QuestionListDto> findQuestionsWithPreview(Pageable pageable) {
        return loadQuestionPort.findQuestionsWithPreview(pageable);
    }

    @Override
    public QuestionWithAnswersDto getQuestionWithAnswers(Long questionId, Long loginUserId, Pageable answerPageable) {
        QuestionDetailDto questionDetailDto =
                loadQuestionPort.findQuestionDetailById(questionId).orElseThrow(QuestionNotFoundException::new);
        Page<AnswerDetailDto> answerDetailDtoPage = loadAnswerPort.findAllByQuestionId(questionId, answerPageable);

        return mapper.toDto(questionDetailDto, answerDetailDtoPage);
    }

    @Override
    @Transactional
    public void updateQuestion(QuestionUpdateDto questionUpdateDto) {
        Question question = loadQuestionPort.findById(questionUpdateDto.id())
                .orElseThrow(QuestionNotFoundException::new);

        ownerShipValidator.validateAuthor(question.getAuthorId(), questionUpdateDto.loginUserId(), QuestionUpdateForbiddenException::new);

        if (hasChanges(question, questionUpdateDto)) {
            question.update(questionUpdateDto.title(), questionUpdateDto.content());
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, Long loginUserId) {
        Question question = loadQuestionPort.findById(questionId)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        ownerShipValidator.validateAuthor(question.getAuthorId(), loginUserId, QuestionDeleteForbiddenException::new);

        deleteQuestionPort.deleteById(question.getId());
    }

    private boolean hasChanges(Question question, QuestionUpdateDto questionUpdateDto) {
        return !Objects.equals(question.getTitle(), questionUpdateDto.title())
                || !Objects.equals(question.getContent(), questionUpdateDto.content());
    }

}