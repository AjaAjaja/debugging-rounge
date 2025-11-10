package ajaajaja.debugging_rounge.feature.answer.application.port.in;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAnswersQuery {
    Page<AnswerDetailDto> getAllByQuestionId(Long questionId, Pageable pageable);
}
