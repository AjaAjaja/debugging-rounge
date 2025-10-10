package ajaajaja.debugging_rounge.feature.answer.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoadAnswerPort {
    Page<AnswerDetailDto> findAllByQuestionId(Long questionId, Pageable pageable);

    Optional<Answer> findById(Long id);
}
