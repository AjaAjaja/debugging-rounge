package ajaajaja.debugging_rounge.feature.question.application.port.in;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionUpdateDto;

public interface UpdateQuestionUseCase {
    void updateQuestion(QuestionUpdateDto questionUpdateDto);
}
