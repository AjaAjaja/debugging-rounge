package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;

import java.util.Optional;

public interface LoadQuestionRecommendPort {
    Optional<QuestionRecommend> findByQuestionIdAndUserId(Long questionId, Long userId);
}
