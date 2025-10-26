package ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.projection;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;

public interface AnswerRecommendScoreAndMyTypeView {
    Long getAnswerId();

    Integer getAnswerRecommendScore();

    RecommendType getMyAnswerRecommendType();
}
