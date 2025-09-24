package ajaajaja.debugging_rounge.feature.answer.application;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.GetAnswersQuery;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerFacade implements CreateAnswerUseCase, GetAnswersQuery {

    private final SaveAnswerPort saveAnswerPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerPort loadAnswerPort;

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

        Page<AnswerDetailDto> answersPage = loadAnswerPort.findPageByQuestionId(questionId, pageable);

        if (answersPage.isEmpty() && !loadQuestionPort.existsQuestionById(questionId)) {
            throw new QuestionNotFoundException();
        }
        return answersPage;
    }

}
