package ajaajaja.debugging_rounge.feature.answer.recommend.api.mapper;

import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.answer.recommend.api.dto.AnswerRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerRecommendMapper {

    @Mapping(target = "userId", source = "loginUserId")
    AnswerRecommendUpdateDto toDto(
            AnswerRecommendUpdateRequest answerRecommendUpdateRequest,
            Long answerId,
            Long loginUserId);

    AnswerRecommendScoreAndMyRecommendTypeResponse toResponse(
            AnswerRecommendScoreAndMyRecommendTypeDto answerRecommendScoreAndMyRecommendTypeDto);
}
