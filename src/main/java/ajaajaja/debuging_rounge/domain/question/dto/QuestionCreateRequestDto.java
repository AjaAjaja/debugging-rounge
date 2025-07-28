package ajaajaja.debuging_rounge.domain.question.dto;

import ajaajaja.debuging_rounge.domain.question.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class QuestionCreateRequestDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(max = 10000, message = "내용은 최대 10,000자까지 작성할 수 있습니다.")
    private String content;

    public Question toEntity(QuestionCreateRequestDto questionCreateRequestDto) {
        return Question.of(questionCreateRequestDto.getTitle(), questionCreateRequestDto.getContent());
    }

}
