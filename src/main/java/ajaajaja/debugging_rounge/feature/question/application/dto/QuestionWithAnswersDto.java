package ajaajaja.debugging_rounge.feature.question.application.dto;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import org.springframework.data.domain.Page;

public record QuestionWithAnswersDto(
        Long questionId,
        String title,
        String content,
        Long authorId,
        String authorEmail,
        Integer recommendScore,
        Page<AnswerDetailDto> answers
) {}
