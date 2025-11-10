package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

public interface LoadQuestionRecommendScorePort {
    Integer findRecommendScoreByQuestionId(Long questionId);
}
