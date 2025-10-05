package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection;

public interface QuestionDetailView {
    Long getId();

    String getTitle();

    String getContent();

    String getEmail();

    Long getAuthorId();
}
