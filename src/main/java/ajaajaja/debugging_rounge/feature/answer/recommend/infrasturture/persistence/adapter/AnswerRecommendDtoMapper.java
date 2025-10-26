package ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.projection.AnswerRecommendScoreAndMyTypeView;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerRecommendDtoMapper {
    List<AnswerRecommendScoreAndMyRecommendTypeDto> toDto(
            List<AnswerRecommendScoreAndMyTypeView> answerRecommendScoreAndMyTypeViews);

}
