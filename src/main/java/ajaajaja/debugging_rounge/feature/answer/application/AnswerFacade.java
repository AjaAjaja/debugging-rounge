package ajaajaja.debugging_rounge.feature.answer.application;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerFacade implements CreateAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final LoadQuestionPort loadQuestionPort;

    @Override
    @Transactional
    public Long createAnswer(AnswerCreateDto answerCreateDto) {

        if (!loadQuestionPort.existsQuestionById(answerCreateDto.questionId())) {
            throw new QuestionNotFoundException();
        }

        Answer savedAnswer = saveAnswerPort.save(answerCreateDto.toEntity());
        return savedAnswer.getId();
    }
}
