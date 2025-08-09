package ajaajaja.debuging_rounge.domain.question.dto;

import ajaajaja.debuging_rounge.domain.question.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class QuestionCreateRequestDto {

    @NotBlank(message = "error.question.title.required")
    @Size(max = 50, message = "error.question.title.size")
    private String title;

    @NotBlank(message = "error.question.content.required")
    @Size(max = 10000, message = "error.question.content.size")
    private String content;

    public Question toEntity(QuestionCreateRequestDto questionCreateRequestDto) {
        return Question.of(questionCreateRequestDto.getTitle(), questionCreateRequestDto.getContent());
    }

}
