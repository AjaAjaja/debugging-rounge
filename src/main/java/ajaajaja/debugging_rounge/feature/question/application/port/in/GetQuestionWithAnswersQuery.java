package ajaajaja.debugging_rounge.feature.question.application.port.in;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import org.springframework.data.domain.Pageable;

public interface GetQuestionWithAnswersQuery {
    QuestionWithAnswersDto getQuestionWithAnswers(Long questionId, Long loginUserId, Pageable answerPageable);
}
