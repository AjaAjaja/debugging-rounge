package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection;

public interface QuestionListView {
    Long getId();

    String getTitle();

    String getPreviewContent();

    String getEmail();

    Integer getRecommendScore();
}
