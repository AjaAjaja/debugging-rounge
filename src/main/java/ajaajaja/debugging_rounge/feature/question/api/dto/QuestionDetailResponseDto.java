package ajaajaja.debugging_rounge.feature.question.api.dto;

import ajaajaja.debugging_rounge.feature.question.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionDetailResponseDto {

    private final Long id;

    private final String title;

    private final String content;

    public static QuestionDetailResponseDto fromEntity(Question question) {
        return new QuestionDetailResponseDto(question.getId(), question.getTitle(), question.getContent());
    }

}
