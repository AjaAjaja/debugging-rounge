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
import java.util.Optional;

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
    public void deleteByAnswerIdAndUserId(Long answerId, Long userId) {
        answerRecommendRepository.deleteByAnswerIdAndUserId(answerId, userId);
    }

    @Override
    public List<AnswerRecommendScoreAndMyRecommendTypeDto> findRecommendScoreAndMyType(List<Long> answerIds, Long userId) {
        List<AnswerRecommendScoreAndMyTypeView> answerRecommendScoreAndMyType =
                answerRecommendRepository.findAnswerRecommendScoreAndMyType(answerIds, userId);
        return mapper.toDto(answerRecommendScoreAndMyType);
    }

    @Override
    public Integer findRecommendScoreByAnswerId(Long answerId) {
        return answerRecommendRepository.findAnswerRecommendScoreByAnswerId(answerId);
    }

    @Override
    public Optional<AnswerRecommend> findByAnswerIdAndUserID(Long answerId, Long userId) {
        return answerRecommendRepository.findAnswerRecommendByAnswerIdAndUserId(answerId, userId);
    }

    @Override
    public void upsert(Long answerId, Long userId, String recommendType) {
        answerRecommendRepository.insertOrUpdate(answerId, userId, recommendType);
    }
}
