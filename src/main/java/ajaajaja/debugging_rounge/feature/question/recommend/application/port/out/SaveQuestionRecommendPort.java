package ajaajaja.debugging_rounge.feature.question.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;

public interface SaveQuestionRecommendPort {
    QuestionRecommend save(QuestionRecommend questionRecommend);
}
