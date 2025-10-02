package ajaajaja.debugging_rounge.feature.question.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionDetailResponse {

    private Long questionId;
    private String title;
    private String content;
    private String authorEmail;
    private Boolean mine;

}
