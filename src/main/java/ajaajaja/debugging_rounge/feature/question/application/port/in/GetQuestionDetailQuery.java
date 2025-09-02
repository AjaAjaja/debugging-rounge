package ajaajaja.debugging_rounge.feature.question.application.port.in;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;

public interface GetQuestionDetailQuery {
    QuestionDetailDto findQuestionById(Long questionId);
}
