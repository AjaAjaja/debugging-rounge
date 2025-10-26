package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

public interface UpsertAnswerRecommendPort {
    void insertOrUpdateAnswerRecommend(Long answerId, Long userId, String recommendType);
}
