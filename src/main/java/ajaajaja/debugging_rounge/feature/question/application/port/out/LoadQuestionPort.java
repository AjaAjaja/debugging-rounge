package ajaajaja.debugging_rounge.feature.question.application.port.out;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoadQuestionPort {
    Optional<Question> findById(Long id);
    Optional<QuestionDetailDto> findQuestionDetailById(Long id);
    Page<QuestionListDto> findQuestionsWithPreview(Pageable pageable);
    Boolean existsQuestionById(Long id);
}
