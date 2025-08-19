package ajaajaja.debugging_rounge.feature.question.api.dto;

import lombok.Getter;

@Getter
public class QuestionDetailResponseDto {

    private final Long questionId;
    private final String title;
    private final String content;
    private final String authorEmail;
    private final Long authorId;
    private Long loginUserId;

    public QuestionDetailResponseDto(Long questionId, String title, String content, String authorEmail, Long authorId) {
        this.questionId = questionId;
        this.title = title;
        this.content = content;
        this.authorEmail = authorEmail;
        this.authorId = authorId;
    }

    public void addLoginUserId(Long loginUserId) {
        this.loginUserId = loginUserId;
    }

}
