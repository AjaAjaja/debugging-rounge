package ajaajaja.debugging_rounge.feature.question.recommend.application;

import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendUpdateDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.in.UpdateQuestionRecommendUseCase;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendScorePort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.SaveQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionRecommendFacade implements UpdateQuestionRecommendUseCase {

    private final SaveQuestionRecommendPort saveQuestionRecommendPort;
    private final LoadQuestionRecommendPort loadQuestionRecommendPort;
    private final LoadQuestionRecommendScorePort loadQuestionRecommendScorePort;
    private final DeleteQuestionRecommendPort deleteQuestionRecommendPort;

    @Override
    @Transactional
    public QuestionRecommendScoreAndMyRecommendTypeDto UpdateQuestionRecommend(QuestionRecommendUpdateDto questionRecommendUpdateDto) {

        RecommendType requestedRecommendType = questionRecommendUpdateDto.recommendType();
        Long questionId = questionRecommendUpdateDto.questionId();
        Long userId = questionRecommendUpdateDto.userId();

        Optional<QuestionRecommend> optionalQuestionRecommend = loadQuestionRecommendPort.
                findByQuestionIdAndUserId(questionId, userId);

        RecommendType finalType = null;

        if (optionalQuestionRecommend.isEmpty()) {
            if (!RecommendType.NONE.equals(requestedRecommendType)) {
                try {
                    QuestionRecommend questionRecommend = QuestionRecommend.of(requestedRecommendType, questionId, userId);
                    saveQuestionRecommendPort.save(questionRecommend);
                    finalType = requestedRecommendType;
                } catch (DataIntegrityViolationException e) {
                    QuestionRecommend questionRecommend = loadQuestionRecommendPort.
                            findByQuestionIdAndUserId(questionId, userId).orElseThrow();
                    if (questionRecommend.getType() != requestedRecommendType) {
                        questionRecommend.updateRecommendType(requestedRecommendType);
                        finalType = questionRecommend.getType();
                    }
                }
            } else finalType = RecommendType.NONE;
        } else {
            QuestionRecommend questionRecommend = optionalQuestionRecommend.get();
            if (!RecommendType.NONE.equals(requestedRecommendType)) {
                questionRecommend.updateRecommendType(requestedRecommendType);
                finalType = questionRecommend.getType();
            } else {
                deleteQuestionRecommendPort.deleteQuestionRecommendByQuestionIdAndUserId(
                        questionRecommend.getQuestionId(), questionRecommend.getUserId());
                finalType = RecommendType.NONE;
            }
        }

        Integer recommendScore = loadQuestionRecommendScorePort
                .getQuestionRecommendScoreByQuestionId(questionId);
        return QuestionRecommendScoreAndMyRecommendTypeDto.of(recommendScore, finalType);
    }
}
