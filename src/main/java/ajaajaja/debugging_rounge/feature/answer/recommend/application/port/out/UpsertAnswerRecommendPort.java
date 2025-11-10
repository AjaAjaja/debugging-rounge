package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

public interface UpsertAnswerRecommendPort {
    void upsert(Long answerId, Long userId, String recommendType);
}
