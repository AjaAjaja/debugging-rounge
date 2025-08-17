package ajaajaja.debugging_rounge.feature.question.api.dto;

import lombok.Getter;

@Getter
public class QuestionListResponseDto {
    private final Long questionId;
    private final String title;
    private final String previewContent;

    public QuestionListResponseDto(Long questionId, String title, String contentSnippet) {
        this.questionId = questionId;
        this.title = title;
        this.previewContent = cleanAndTrim(contentSnippet);
    }

    private String cleanAndTrim(String rawContent) {
        if (rawContent == null) return "";
        String cleaned = rawContent
                .replaceAll("[\\r\\n]+", " ")   // 줄바꿈 제거
                .replaceAll("\\s{2,}", " ")     // 연속 공백 하나로
                .trim(); // 앞뒤 공백 제거

        return cleaned.length() > 50
                ? cleaned.substring(0, 50) + "..."
                : cleaned;
    }
}
