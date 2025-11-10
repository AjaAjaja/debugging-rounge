package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

public interface DeleteQuestionRecommendPort {
    void deleteByQuestionIdAndUserId(Long questionId, Long userId);
}
