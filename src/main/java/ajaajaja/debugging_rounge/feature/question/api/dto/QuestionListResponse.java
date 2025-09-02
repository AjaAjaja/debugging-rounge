package ajaajaja.debugging_rounge.feature.question.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionListResponse {
    private final Long questionId;
    private final String title;
    private final String previewContent;
    private final String authorEmail;
}
