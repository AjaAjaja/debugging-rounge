package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

public interface DeleteAnswerRecommendPort {
    void deleteByAnswerIdAndUserId(Long answerId, Long userId);
}
