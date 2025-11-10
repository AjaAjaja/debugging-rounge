package ajaajaja.debugging_rounge.feature.question.application.port.in;

import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetQuestionListWithPreviewQuery {
    Page<QuestionListDto> getQuestionsWithPreview(Pageable pageable, QuestionOrder sort);
}
