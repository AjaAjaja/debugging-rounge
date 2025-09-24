package ajaajaja.debugging_rounge.feature.answer.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAnswerPort {
    Page<AnswerDetailDto> findPageByQuestionId(Long questionId, Pageable pageable);
}
