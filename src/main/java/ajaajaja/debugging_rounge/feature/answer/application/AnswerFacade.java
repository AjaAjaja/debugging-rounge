package ajaajaja.debugging_rounge.feature.answer.application;

import ajaajaja.debugging_rounge.common.security.validator.OwnerShipValidator;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerUpdateDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.GetAnswersQuery;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.UpdateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerNotFoundException;
import ajaajaja.debugging_rounge.feature.answer.domain.exception.AnswerUpdateForbiddenException;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerFacade implements CreateAnswerUseCase, GetAnswersQuery, UpdateAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerPort loadAnswerPort;
    private final OwnerShipValidator ownerShipValidator;

    @Override
    @Transactional
    public Long createAnswer(AnswerCreateDto answerCreateDto) {

        if (!loadQuestionPort.existsQuestionById(answerCreateDto.questionId())) {
            throw new QuestionNotFoundException();
        }

        Answer savedAnswer = saveAnswerPort.save(answerCreateDto.toEntity());
        return savedAnswer.getId();
    }

    @Override
    public Page<AnswerDetailDto> findAllByQuestionId(Long questionId, Pageable pageable) {

        Page<AnswerDetailDto> answersPage = loadAnswerPort.findAllByQuestionId(questionId, pageable);

        if (answersPage.isEmpty() && !loadQuestionPort.existsQuestionById(questionId)) {
            throw new QuestionNotFoundException();
        }
        return answersPage;
    }

    @Override
    @Transactional
    public void updateAnswer(AnswerUpdateDto answerUpdateDto) {
        Answer answer = loadAnswerPort.findById(answerUpdateDto.id()).orElseThrow(AnswerNotFoundException::new);

        ownerShipValidator.validateAuthor(answer.getAuthorId(), answerUpdateDto.authorId(), AnswerUpdateForbiddenException::new);

        if (hasChanges(answer, answerUpdateDto)) {
            answer.update(answerUpdateDto.content());
        }
    }

    private boolean hasChanges(Answer answer, AnswerUpdateDto answerUpdateDto) {
        return !Objects.equals(answer.getContent(), answerUpdateDto.content());
    }
}
