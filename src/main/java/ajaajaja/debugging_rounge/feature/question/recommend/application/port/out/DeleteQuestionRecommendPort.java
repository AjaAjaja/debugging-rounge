package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

public interface DeleteQuestionRecommendPort {
    void deleteQuestionRecommendByQuestionIdAndUserId(Long questionId, Long userId);
}
