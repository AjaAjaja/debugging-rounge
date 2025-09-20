package ajaajaja.debugging_rounge.feature.answer.application.port.in;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerCreateDto;

public interface CreateAnswerUseCase {
    Long createAnswer(AnswerCreateDto answerCreateDto);
}
