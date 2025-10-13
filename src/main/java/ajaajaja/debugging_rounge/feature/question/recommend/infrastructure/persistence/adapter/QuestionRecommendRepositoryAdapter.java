package ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendScorePort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.SaveQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence.QuestionRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionRecommendRepositoryAdapter
        implements LoadQuestionRecommendPort, SaveQuestionRecommendPort, LoadQuestionRecommendScorePort,
        DeleteQuestionRecommendPort {

    private final QuestionRecommendRepository questionRecommendRepository;
    @Override
    @Transactional
    public QuestionRecommend save(QuestionRecommend questionRecommend) {
        return questionRecommendRepository.save(questionRecommend);
    }

    @Override
    public Optional<QuestionRecommend> findByQuestionIdAndUserId(Long questionId, Long userId) {
        return questionRecommendRepository.findByQuestionIdAndUserId(questionId, userId);
    }

    @Override
    public Integer getQuestionRecommendScoreByQuestionId(Long questionId) {
        return questionRecommendRepository.getQuestionRecommendScoreByQuestionId(questionId);
    }

    @Override
    public void deleteQuestionRecommendByQuestionIdAndUserId(Long questionId, Long userId) {
        questionRecommendRepository.deleteByQuestionIdAndUserId(questionId, userId);
    }
}
