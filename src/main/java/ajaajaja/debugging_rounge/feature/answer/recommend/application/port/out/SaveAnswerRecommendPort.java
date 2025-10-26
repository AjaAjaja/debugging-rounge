package ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;

public interface SaveAnswerRecommendPort {
    AnswerRecommend save(AnswerRecommend answerRecommend);
}
