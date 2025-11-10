package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

public interface UpsertQuestionRecommendPort {
    void upsert(Long questionId, Long userId, String type);
}
