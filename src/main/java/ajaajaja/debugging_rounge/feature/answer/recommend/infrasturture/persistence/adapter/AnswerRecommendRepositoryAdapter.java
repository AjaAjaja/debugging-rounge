package ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.DeleteAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.LoadAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.SaveAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.UpsertAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;
import ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.AnswerRecommendRepository;
import ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.projection.AnswerRecommendScoreAndMyTypeView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerRecommendRepositoryAdapter
        implements SaveAnswerRecommendPort, LoadAnswerRecommendPort,
        UpsertAnswerRecommendPort, DeleteAnswerRecommendPort {

    private final AnswerRecommendRepository answerRecommendRepository;
    private final AnswerRecommendDtoMapper mapper;

    @Override
    public AnswerRecommend save(AnswerRecommend answerRecommend) {
        return answerRecommendRepository.save(answerRecommend);
    }

    @Override
    public void deleteAnswerRecommendByAnswerIdAndUserId(Long answerId, Long userId) {
        answerRecommendRepository.deleteByAnswerIdAndUserId(answerId, userId);
    }

    @Override
    public List<AnswerRecommendScoreAndMyRecommendTypeDto> getAnswerRecommendScoreAndMyType(List<Long> answerIds, Long userId) {
        List<AnswerRecommendScoreAndMyTypeView> answerRecommendScoreAndMyType =
                answerRecommendRepository.getAnswerRecommendScoreAndMyType(answerIds, userId);
        return mapper.toDto(answerRecommendScoreAndMyType);
    }

    @Override
    public Integer getAnswerRecommendScoreByAnswerId(Long answerId) {
        return answerRecommendRepository.getAnswerRecommendScoreByAnswerId(answerId);
    }

    @Override
    public void insertOrUpdateAnswerRecommend(Long answerId, Long userId, String recommendType) {
        answerRecommendRepository.insertOrUpdate(answerId, userId, recommendType);
    }
}
