package ajaajaja.debugging_rounge.feature.answer.application;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerFacade implements CreateAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;

    @Override
    @Transactional
    public Long createAnswer(AnswerCreateDto answerCreateDto) {
        Answer savedAnswer = saveAnswerPort.save(answerCreateDto.toEntity());
        return savedAnswer.getId();
    }
}
