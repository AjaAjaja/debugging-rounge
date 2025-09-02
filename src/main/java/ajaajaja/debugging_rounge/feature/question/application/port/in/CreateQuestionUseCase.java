package ajaajaja.debugging_rounge.feature.question.application.port.in;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionCreateDto;

public interface CreateQuestionUseCase {
    Long createQuestion(QuestionCreateDto questionCreateDto);
}
